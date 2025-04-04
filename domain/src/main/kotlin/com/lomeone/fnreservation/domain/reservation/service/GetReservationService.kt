package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.entity.ReservationStatus
import com.lomeone.fnreservation.domain.reservation.exception.ReservationNotFoundException
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import org.springframework.stereotype.Service

@Service
class GetReservationService(
    private val reservationRepository: ReservationRepository
) {
    fun getReservation(query: GetReservationQuery): GetReservationResult {
        val reservation = findReservation(query)

        return GetReservationResult(
            gameType = reservation.gameType,
            session = reservation.session,
            reservation = reservation.reservation,
            status = reservation.status
        )
    }

    private fun findReservation(query: GetReservationQuery) =
        reservationRepository.findByStoreBranchAndLatestGameType(query.storeBranch, query.gameType)
            ?: throw ReservationNotFoundException(detail = mapOf("storeBranch" to query.storeBranch, "gameType" to query.gameType))
}

data class GetReservationQuery(
    val storeBranch: String,
    val gameType: String
)

data class GetReservationResult(
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>,
    val status: ReservationStatus
)
