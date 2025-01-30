package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.exception.ReservationNotFoundException
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository

class GetReservationService(
    private val reservationRepository: ReservationRepository
) {
    fun getReservation(query: GetReservationQuery): GetReservationResult {
        val reservation = reservationRepository.findByStoreBranchAndLatestGameType(query.storeBranch, query.gameType)
            ?: throw ReservationNotFoundException(detail = mapOf("storeBranch" to query.storeBranch, "gameType" to query.gameType))
        return GetReservationResult(
            gameType = reservation.gameType,
            session = reservation.session,
            reservation = reservation.reservation
        )
    }
}

data class GetReservationQuery(
    val storeBranch: String,
    val gameType: String
)

data class GetReservationResult(
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>
)
