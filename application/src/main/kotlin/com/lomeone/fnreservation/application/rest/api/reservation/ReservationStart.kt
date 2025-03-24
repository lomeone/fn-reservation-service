package com.lomeone.fnreservation.application.rest.api.reservation

data class ReservationStartRequest(
    val storeBranch: String,
    val gameType: String,
    val session: Int? = null
)

data class ReservationStartResponse(
    val storeBranch: String,
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>
)
