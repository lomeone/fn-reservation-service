package com.lomeone.fnreservation.application.rest.api.reservation

data class CancelReservationRequest(
    val storeBranch: String,
    val gameType: String,
    val cancelUsers: Set<String>
)

data class CancelReservationResponse(
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>
)
