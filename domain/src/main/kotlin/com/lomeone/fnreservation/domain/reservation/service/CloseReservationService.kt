package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository

class CloseReservationService(
    private val reservationRepository: ReservationRepository
) {
    suspend fun closeReservation(command: CloseReservationCommand): CloseReservationResult {
        val reservation = reservationRepository.findByStoreBranchAndLatestGameType(command.storeBranch, command.gameType) ?: throw Exception("예약을 찾을 수 없습니다")

        if (reservation.isClosed()) {
            throw Exception("예약이 마감되었습니다")
        }

        reservation.closeReservation()

        val savedReservation = reservationRepository.save(reservation)

        return CloseReservationResult(
            storeBranch = savedReservation.storeBranch,
            gameType = savedReservation.gameType,
            session = savedReservation.session
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
