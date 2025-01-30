package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.exception.AlreadyReservedSessionException
import com.lomeone.fnreservation.domain.reservation.exception.ReservationInProgressException
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository

class StartReservationService(
    private val reservationRepository: ReservationRepository
) {
    fun startReservation(command: StartReservationCommand): StartReservationResult {
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

    private fun getSessionOfReservation(command: StartReservationCommand) =
        if (command.session != null) {
            ensureUniqueSession(command.storeBranch, command.gameType, command.session)
            command.session
        } else {
            val reservation = getReservation(command)
            ensureReservationClosed(reservation)
            reservation.session + 1
        }

    private fun ensureUniqueSession(storeBranch: String, gameType: String, session: Int) {
        if (reservationRepository.findByStoreBranchAndGameTypeAndSession(storeBranch, gameType, session) != null) {
            throw AlreadyReservedSessionException(
                detail = mapOf(
                    "storeBranch" to storeBranch,
                    "gameType" to gameType,
                    "session" to session
                )
            )
        }
    }

    private fun getReservation(command: StartReservationCommand): Reservation =
        reservationRepository.findByStoreBranchAndLatestGameType(command.storeBranch, command.gameType) ?: throw Exception("예약을 찾을 수 없습니다")

    private fun ensureReservationClosed(reservation: Reservation) {
        reservation.isOpen() && throw ReservationInProgressException(
            detail = mapOf(
                "storeBranch" to reservation.storeBranch,
                "gameType" to reservation.gameType,
                "session" to reservation.session
            )
        )
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
