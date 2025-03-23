package com.lomeone.fnreservation.application.rest.api.reservation

data class ReservationRequest(
    val storeBranch: String,
    val gameType: String,
    val reservationUsers: List<String>,
    val reservationTime: String
)

data class ReservationResponse(
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>
)
