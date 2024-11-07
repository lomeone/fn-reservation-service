package com.lomeone.com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import kotlinx.serialization.Serializable

class ReserveService(
    private val reservationRepository: ReservationRepository
) {
    suspend fun reserve(command: ReserveCommand): ReserveResult {
        val reservation = reservationRepository.findByStoreBranchAndLatestGameType(command.storeBranch, command.gameType) ?: throw Exception("예약을 찾을 수 없습니다")

        command.reservationUsers.forEach {
            reservation.reserve(it, command.reservationTime)
        }

        reservationRepository.save(reservation)

        return ReserveResult(
            storeBranch = reservation.storeBranch,
            gameType = reservation.gameType,
            reservation = reservation.reservation
        )
    }
}

data class ReserveCommand(
    val storeBranch: String,
    val gameType: String,
    val reservationUsers: Set<String>,
    val reservationTime: String
)

@Serializable
data class ReserveResult(
    val storeBranch: String,
    val gameType: String,
    val reservation: Map<String, String>
)