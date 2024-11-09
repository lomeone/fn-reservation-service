package com.lomeone.fnreservation.application.rest.api.reservation

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/reservation")
class Reservation

@Serializable
data class ReservationRequest(
    val storeBranch: String,
    val gameType: String,
    val reservationUsers: Set<String>,
    val reservationTime: String
)

@Serializable
data class ReservationResponse(
    val storeBranch: String,
    val gameType: String,
    val reservation: Map<String, String>
)
