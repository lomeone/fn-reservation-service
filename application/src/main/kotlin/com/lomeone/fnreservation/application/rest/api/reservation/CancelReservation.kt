package com.lomeone.fnreservation.application.rest.api.reservation

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/reservation/cancel")
class CancelReservation

@Serializable
data class CancelReservationRequest(
    val storeBranch: String,
    val gameType: String,
    val cancelUsers: Set<String>
)

@Serializable
data class CancelReservationResponse(
    val storeBranch: String,
    val gameType: String,
    val reservation: Map<String, String>
)
