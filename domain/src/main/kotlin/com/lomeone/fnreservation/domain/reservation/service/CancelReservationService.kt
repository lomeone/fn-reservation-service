package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository

class CancelReservationService(
    private val reservationRepository: ReservationRepository
) {
    suspend fun cancel(command: CancelCommand): CancelResult {
        val reservation = reservationRepository.findByStoreBranchAndLatestGameType(command.storeBranch, command.gameType) ?: throw Exception("예약을 찾을 수 없습니다")

        if (reservation.isClosed()) {
            throw Exception("예약이 마감되었습니다")
        }

        command.cancelUsers.forEach {
            reservation.cancel(it)
        }

        val savedReservation = reservationRepository.save(reservation)

        return CancelResult(
            storeBranch = savedReservation.storeBranch,
            gameType = savedReservation.gameType,
            reservation = savedReservation.reservation
        )
    }
}

data class CancelCommand(
    val storeBranch: String,
    val gameType: String,
    val cancelUsers: Set<String>
)

data class CancelResult(
    val storeBranch: String,
    val gameType: String,
    val reservation: Map<String, String>
)
