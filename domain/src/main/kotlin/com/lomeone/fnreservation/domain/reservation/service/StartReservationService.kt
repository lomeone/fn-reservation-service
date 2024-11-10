package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository

class StartReservationService(
    private val reservationRepository: ReservationRepository
) {
    suspend fun startReservation(command: StartReservationCommand): StartReservationResult {
        val session = getSessionOfReservation(command)

        val reservation = reservationRepository.save(
                Reservation(
                storeBranch = command.storeBranch,
                gameType = command.gameType,
                session = session
            )
        )

        return StartReservationResult(
            storeBranch = reservation.storeBranch,
            gameType = reservation.gameType,
            session = reservation.session,
            reservation = reservation.reservation
        )
    }

    private suspend fun getSessionOfReservation(command: StartReservationCommand) =
        if (command.session != null) {
            ensureSessionIsNotDuplicated(command.storeBranch, command.gameType, command.session)
            command.session
        } else {
            val reservation = getReservation(command)
            checkReservationNotOpen(reservation)
            reservation.session + 1
        }

    private suspend fun ensureSessionIsNotDuplicated(storeBranch: String, gameType: String, session: Int) {
        if (reservationRepository.findByStoreBranchAndGameTypeAndSession(storeBranch, gameType, session) != null) {
            throw Exception("이미 예약된 세션입니다.")
        }
    }

    private suspend fun getReservation(command: StartReservationCommand): Reservation =
        reservationRepository.findByStoreBranchAndLatestGameType(command.storeBranch, command.gameType) ?: throw Exception("예약을 찾을 수 없습니다")

    private fun checkReservationNotOpen(reservation: Reservation) {
        reservation.isOpen() && throw Exception("아직 예약 중입니다.")
    }
}

data class StartReservationCommand(
    val storeBranch: String,
    val gameType: String,
    val session: Int? = null
)

data class StartReservationResult(
    val storeBranch: String,
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>
)
