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
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>
)
