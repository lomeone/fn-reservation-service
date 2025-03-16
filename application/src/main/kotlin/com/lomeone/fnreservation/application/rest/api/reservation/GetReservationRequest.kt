package com.lomeone.fnreservation.application.rest.api.reservation

data class GetReservationRequest(
    val storeBranch: String,
    val gameType: String
)

data class GetReservationResponse(
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>,
    val status: String
)
