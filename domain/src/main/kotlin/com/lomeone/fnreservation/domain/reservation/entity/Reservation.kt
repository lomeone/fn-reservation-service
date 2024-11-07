package com.lomeone.fnreservation.domain.reservation.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Reservation(
    @BsonId
    val id: ObjectId = ObjectId(),
    val storeBranch: String,
    val gameType: String,
    val session: Int,
    @BsonProperty("reservation")
    val _reservation: MutableMap<String, String> = mutableMapOf(),
    var status: ReservationStatus = ReservationStatus.OPEN,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val reservation: Map<String, String>
        get() = this._reservation.toMap()

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