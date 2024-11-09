package com.lomeone.fnreservation.application.rest.api.reservation

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/reservation")
@Serializable
data class GetReservation(
    val storeBranch: String,
    val gameType: String
)

@Serializable
data class GetReservationResponse(
    val gameType: String,
    val reservation: Map<String, String>
)