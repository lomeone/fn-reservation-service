package com.lomeone.fnreservation.domain.reservation.repository

import com.lomeone.fnreservation.domain.reservation.entity.Reservation

interface ReservationRepository {
    fun save(reservation: Reservation): Reservation
    fun findByIdAndSession(id: String, session: Int): Reservation?
    fun findByStoreBranchAndLatestGameType(storeBranch: String, gameType: String): Reservation?
    fun findByStoreBranchAndGameTypeAndSession(storeBranch: String, gameType: String, session: Int): Reservation?
}
