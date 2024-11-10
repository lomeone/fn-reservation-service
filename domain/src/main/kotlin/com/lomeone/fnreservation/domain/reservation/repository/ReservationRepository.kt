package com.lomeone.fnreservation.domain.reservation.repository

import com.lomeone.fnreservation.domain.reservation.entity.Reservation

interface ReservationRepository {
    suspend fun findByStoreBranchAndLatestGameType(storeBranch: String, gameType: String): Reservation?
    suspend fun findByStoreBranchAndGameTypeAndSession(storeBranch: String, gameType: String, session: Int): Reservation?
    suspend fun save(reservation: Reservation): Reservation
}
