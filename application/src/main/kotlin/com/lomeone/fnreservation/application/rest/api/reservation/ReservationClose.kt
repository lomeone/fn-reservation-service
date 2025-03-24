package com.lomeone.fnreservation.application.rest.api.reservation

class ReservationClose

data class ReservationCloseRequest(
    val storeBranch: String,
    val gameType: String
)

data class ReservationCloseResponse(
    val storeBranch: String,
    val gameType: String,
    val session: Int
)
