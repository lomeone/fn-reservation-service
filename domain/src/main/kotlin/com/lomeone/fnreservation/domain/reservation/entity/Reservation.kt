package com.lomeone.fnreservation.domain.reservation.entity

import org.bson.codecs.pojo.annotations.BsonProperty
import java.security.MessageDigest
import java.time.ZonedDateTime

class Reservation(
    id: String? = null,
    val storeBranch: String,
    val gameType: String,
    val session: Int,
    @BsonProperty("reservation")
    reservation: LinkedHashMap<String, String> = linkedMapOf(),
    var status: ReservationStatus = ReservationStatus.OPEN,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
) {
    val id: String = id ?: MessageDigest.getInstance("SHA-256")
        .digest("${storeBranch}_${gameType}_${session}".toByteArray())
        .joinToString("") { "%02x".format(it) }

    private val _reservation: LinkedHashMap<String, String> = reservation
    val reservation: Map<String, String> get() = this._reservation.toMap()

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
