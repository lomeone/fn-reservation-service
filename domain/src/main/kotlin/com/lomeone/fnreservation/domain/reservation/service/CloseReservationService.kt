package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.exception.ReservationClosedException
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository

class CloseReservationService(
    private val reservationRepository: ReservationRepository
) {
    suspend fun closeReservation(command: CloseReservationCommand): CloseReservationResult {
        val reservation = reservationRepository.findByStoreBranchAndLatestGameType(command.storeBranch, command.gameType) ?: throw Exception("예약을 찾을 수 없습니다")

        ensureReservationOpened(reservation)

        reservation.closeReservation()

        val savedReservation = reservationRepository.save(reservation)

        return CloseReservationResult(
            storeBranch = savedReservation.storeBranch,
            gameType = savedReservation.gameType,
            session = savedReservation.session
        )
    }

    private fun ensureReservationOpened(reservation: Reservation) {
        reservation.isClosed() && throw ReservationClosedException(
            detail = mapOf(
                "storeBranch" to reservation.storeBranch,
                "gameType" to reservation.gameType,
                "session" to reservation.session
            )
        )
    }
}

data class CloseReservationCommand(
    val storeBranch: String,
    val gameType: String
)

data class CloseReservationResult(
    val storeBranch: String,
    val gameType: String,
    val session: Int
)
