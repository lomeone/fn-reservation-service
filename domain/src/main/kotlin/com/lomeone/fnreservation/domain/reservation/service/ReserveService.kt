package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.exception.ReservationNotFoundException
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository

class ReserveService(
    private val reservationRepository: ReservationRepository
) {
    suspend fun reserve(command: ReserveCommand): ReserveResult {
        val reservation = reservationRepository.findByStoreBranchAndLatestGameType(command.storeBranch, command.gameType)
            ?: throw ReservationNotFoundException(detail = mapOf("storeBranch" to command.storeBranch, "gameType" to command.gameType))

        if (reservation.isClosed()) {
            throw Exception("예약이 마감되었습니다")
        }

        command.reservationUsers.forEach {
            reservation.reserve(it, command.reservationTime)
        }

        val savedReservation = reservationRepository.save(reservation)

        return ReserveResult(
            storeBranch = savedReservation.storeBranch,
            gameType = savedReservation.gameType,
            reservation = savedReservation.reservation
        )
    }
}

data class ReserveCommand(
    val storeBranch: String,
    val gameType: String,
    val reservationUsers: Set<String>,
    val reservationTime: String
)

data class ReserveResult(
    val storeBranch: String,
    val gameType: String,
    val reservation: Map<String, String>
)
