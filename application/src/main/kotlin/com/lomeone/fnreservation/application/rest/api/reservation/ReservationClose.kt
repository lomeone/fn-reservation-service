package com.lomeone.fnreservation.application.rest.api.reservation

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/reservation/close")
class ReservationClose

@Serializable
data class ReservationCloseRequest(
    val storeBranch: String,
    val gameType: String
)

@Serializable
data class ReservationCloseResponse(
    val storeBranch: String,
    val gameType: String,
    val session: Int
)
