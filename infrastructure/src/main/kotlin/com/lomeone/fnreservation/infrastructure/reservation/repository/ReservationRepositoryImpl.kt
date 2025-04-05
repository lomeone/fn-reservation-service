package com.lomeone.fnreservation.infrastructure.reservation.repository

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.entity.ReservationStatus
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import com.lomeone.fnreservation.infrastructure.reservation.exception.DynamoReservationPutItemNotFoundException
import com.lomeone.fnreservation.infrastructure.reservation.repository.ReservationDynamo.Entry
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import java.security.MessageDigest
import java.time.ZonedDateTime

@Repository
class ReservationRepositoryImpl(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient
) : ReservationRepository {

    private val table = dynamoDbEnhancedClient.table("fn-reservation-v2", TableSchema.fromBean(ReservationDynamo::class.java))

    override fun save(reservation: Reservation): Reservation {
        return if (isAlreadyExistReservation(reservation)) {
            table.updateItem(ReservationDynamo(reservation = reservation)).toReservation()
        } else {
            table.putItem(ReservationDynamo(reservation = reservation))
            val partitionKey = generatePartitionKey(reservation.storeBranch, reservation.gameType)
            this.findByIdAndSession(partitionKey, reservation.session) ?: throw DynamoReservationPutItemNotFoundException(
                detail = mapOf(
                    "id" to reservation.id,
                    "storeBranch" to reservation.storeBranch,
                    "gameType" to reservation.gameType,
                    "session" to reservation.session
                )
            )
        }
    }

    private fun isAlreadyExistReservation(reservation: Reservation) =
        reservation.id.isNotBlank() && findByIdAndSession(reservation.id, reservation.session) != null

    override fun findByIdAndSession(id: String, session: Int): Reservation? =
        table.getItem(Key.builder().partitionValue(id).sortValue(session).build())?.toReservation()

    override fun findByStoreBranchAndLatestGameType(storeBranch: String, gameType: String): Reservation? {
        val partitionKey = generatePartitionKey(storeBranch, gameType)

        val page = table.query { query ->
            query.queryConditional(
                QueryConditional.keyEqualTo(
                    Key.builder().partitionValue(partitionKey).build()
                )
            ).scanIndexForward(false).limit(1)
        }.firstOrNull()

        val reservations = page?.items()

        return reservations?.firstOrNull()?.toReservation()
    }

    private fun generatePartitionKey(storeBranch: String, gameType: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest("${storeBranch}_${gameType}".toByteArray())
            .joinToString("") { "%02x".format(it) }

    override fun findByStoreBranchAndGameTypeAndSession(storeBranch: String, gameType: String, session: Int): Reservation? {
        val partitionKey = generatePartitionKey(storeBranch, gameType)

        val key = Key.builder().partitionValue(partitionKey).sortValue(session).build()

        val item = table.getItem(key)

        return item?.toReservation()
    }
}

@DynamoDbBean
class ReservationDynamo(
    @get:DynamoDbPartitionKey
    var reservations_id: String,
    var storeBranch: String,
    var gameType: String,
    @get:DynamoDbSortKey
    var session: Int = 0,
    var reservation: List<Entry>,
    var status: ReservationStatus,
    var createdAt: ZonedDateTime,
    var updatedAt: ZonedDateTime
) {
    @DynamoDbBean
    data class Entry(var key: String = "", var value: String = "")

    init {
        if (reservations_id.isBlank()) {
            reservations_id = MessageDigest.getInstance("SHA-256")
                .digest("${storeBranch}_${gameType}".toByteArray())
                .joinToString("") { "%02x".format(it) }
        }
    }

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
