package com.lomeone.fnreservation.domain.reservation.service

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.exception.ReservationClosedException
import com.lomeone.fnreservation.domain.reservation.exception.ReservationNotFoundException
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import org.springframework.stereotype.Service

@Service
class ReserveService(
    private val reservationRepository: ReservationRepository
) {
    fun reserve(command: ReserveCommand): ReserveResult {
        val reservation = findReservation(command)

        ensureReservationOpen(reservation)

        command.reservationUsers.forEach {
            reservation.reserve(it, command.reservationTime)
        }

        val savedReservation = reservationRepository.save(reservation)

        return ReserveResult(
            gameType = savedReservation.gameType,
            session = savedReservation.session,
            reservation = savedReservation.reservation
        )
    }

    private fun findReservation(command: ReserveCommand): Reservation =
        reservationRepository.findByStoreBranchAndLatestGameType(command.storeBranch, command.gameType)
            ?: throw ReservationNotFoundException(detail = mapOf("storeBranch" to command.storeBranch, "gameType" to command.gameType))

    private fun ensureReservationOpen(reservation: Reservation) {
        reservation.isClosed() && throw ReservationClosedException(
            detail = mapOf(
                "storeBranch" to reservation.storeBranch,
                "gameType" to reservation.gameType,
                "session" to reservation.session
            )
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
    val gameType: String,
    val session: Int,
    val reservation: Map<String, String>
)
