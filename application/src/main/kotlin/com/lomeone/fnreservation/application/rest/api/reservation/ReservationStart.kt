package com.lomeone.fnreservation.application.rest.api.reservation

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/reservation/start")
class ReservationStart

@Serializable
data class ReservationStartRequest(
    val storeBranch: String,
    val gameType: String
)

@Serializable
data class ReservationStartResponse(
    val storeBranch: String,
    val gameType: String,
    val session: Int
)