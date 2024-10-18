package com.lomeone.com.lomeone.domain.reservation.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

data class Reservation(
    @BsonId
    val id: ObjectId = ObjectId(),
    val gameType: String,
    val session: String,
    @BsonProperty("reservation")
    private val _reservation: MutableMap<String, String> = mutableMapOf(),
    private var status: ReservationStatus = ReservationStatus.OPEN
) {
    val reservation: Map<String, String>
        get() = _reservation.toMap()

    fun getStatus() = status

    fun reserve(name: String, time: String) {
        _reservation.put(name, time)
    }

    fun cancel(name: String) {
        _reservation.remove(name)
    }

    fun closeReservation() {
        status = ReservationStatus.CLOSED
    }
}

enum class ReservationStatus {
    OPEN,
    CLOSED
}