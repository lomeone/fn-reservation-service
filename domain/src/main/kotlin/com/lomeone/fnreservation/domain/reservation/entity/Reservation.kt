package com.lomeone.fnreservation.domain.reservation.entity

import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.time.LocalDateTime

@DynamoDbBean
class Reservation(
    @get:DynamoDbPartitionKey
    val id: ObjectId = ObjectId(),
    val storeBranch: String,
    val gameType: String,
    val session: Int,
    @BsonProperty("reservation")
    reservation: LinkedHashMap<String, String> = linkedMapOf(),
    var status: ReservationStatus = ReservationStatus.OPEN
) {
    private val _reservation: LinkedHashMap<String, String> = reservation
    val reservation: Map<String, String> get() = this._reservation.toMap()

    var createdAt: LocalDateTime = LocalDateTime.now()
    var updatedAt: LocalDateTime = LocalDateTime.now()

    fun reserve(name: String, time: String) {
        this._reservation.put(name, time)
    }

    fun cancel(name: String) {
        this._reservation.remove(name)
    }

    fun closeReservation() {
        this.status = ReservationStatus.CLOSED
    }

    fun isOpen() = this.status == ReservationStatus.OPEN

    fun isClosed() = this.status == ReservationStatus.CLOSED
}

enum class ReservationStatus {
    OPEN,
    CLOSED
}
