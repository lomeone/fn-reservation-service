package com.lomeone.fnreservation.infrastructure.reservation.repository

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.entity.ReservationStatus
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import com.lomeone.fnreservation.infrastructure.reservation.exception.DynamoReservationPutItemNotFoundException
import com.lomeone.fnreservation.infrastructure.reservation.repository.ReservationDynamo.Entry
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.ZonedDateTime

@Repository
class ReservationRepositoryImpl(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient
) : ReservationRepository {

    private val table = dynamoDbEnhancedClient.table("fn-reservations", TableSchema.fromBean(ReservationDynamo::class.java))

    override fun save(reservation: Reservation): Reservation {
        return if (isAlreadyExistReservation(reservation)) {
            table.updateItem(ReservationDynamo(reservation = reservation)).toReservation()
        } else {
            table.putItem(ReservationDynamo(reservation = reservation))
            this.findById(reservation.id) ?: throw DynamoReservationPutItemNotFoundException(
                detail = mapOf(
                    "id" to reservation.id,
                    "storeBranch" to reservation.storeBranch,
                    "gameType" to reservation.gameType,
                    "session" to reservation.session
                )
            )
        }
    }

    private fun isAlreadyExistReservation(reservation: Reservation) = findById(reservation.id) != null

    override fun findById(id: String): Reservation? =
        table.getItem(Key.builder().partitionValue(id).build())?.toReservation()

    override fun findByStoreBranchAndLatestGameType(storeBranch: String, gameType: String): Reservation? {
        val items = table.scan(
            ScanEnhancedRequest.builder()
                .filterExpression(
                    Expression.builder()
                        .expression("storeBranch = :storeBranch AND gameType = :gameType")
                        .putExpressionValue(":storeBranch", AttributeValue.builder().s(storeBranch).build())
                        .putExpressionValue(":gameType", AttributeValue.builder().s(gameType).build())
                        .build()
                ).build()
        ).items()
        val reservations = items.sortedBy { it.session }

        return reservations.lastOrNull()?.toReservation()
    }

    override fun findByStoreBranchAndGameTypeAndSession(storeBranch: String, gameType: String, session: Int): Reservation? {
        val items = table.scan(
            ScanEnhancedRequest.builder()
                .filterExpression(
                    Expression.builder()
                        .expression("storeBranch = :storeBranch AND gameType = :gameType AND #session = :session")
                        .putExpressionValue(":storeBranch", AttributeValue.builder().s(storeBranch).build())
                        .putExpressionValue(":gameType", AttributeValue.builder().s(gameType).build())
                        .putExpressionName("#session", "session")
                        .putExpressionValue(":session", AttributeValue.builder().n(session.toString()).build())
                        .build()
                )
                .build()
        ).items()
        return items.firstOrNull()?.toReservation()
    }
}

@DynamoDbBean
class ReservationDynamo(
    @get:DynamoDbPartitionKey
    var reservations_id: String,
    var storeBranch: String,
    var gameType: String,
    var session: Int = 0,
    var reservation: List<Entry>,
    var status: ReservationStatus,
    var createdAt: ZonedDateTime,
    var updatedAt: ZonedDateTime
) {
    @DynamoDbBean
    data class Entry(var key: String = "", var value: String = "")

    constructor() : this(
        reservations_id = "",
        storeBranch = "",
        gameType = "",
        status = ReservationStatus.OPEN,
        reservation = listOf(),
        createdAt = ZonedDateTime.now(),
        updatedAt = ZonedDateTime.now()
    )

    constructor(reservation: Reservation) : this(
        reservations_id = reservation.id,
        storeBranch = reservation.storeBranch,
        gameType = reservation.gameType,
        session = reservation.session,
        reservation = reservation.reservation.toEntryList(),
        status = reservation.status,
        createdAt = reservation.createdAt,
        updatedAt = ZonedDateTime.now()
    )

    fun toReservation(): Reservation =
        Reservation(
            id = this.reservations_id,
            storeBranch = this.storeBranch,
            gameType = this.gameType,
            session = this.session,
            reservation = reservation.toMap(),
            status = this.status,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
}


private fun Map<String, String>.toEntryList(): List<Entry> =
    this.map { Entry(it.key, it.value) }

private fun List<Entry>.toMap(): LinkedHashMap<String, String> =
    LinkedHashMap<String, String>().apply {
        this@toMap.forEach { put(it.key, it.value) }
    }
