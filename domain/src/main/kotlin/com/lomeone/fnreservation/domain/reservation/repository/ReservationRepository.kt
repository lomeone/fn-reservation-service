package com.lomeone.fnreservation.domain.reservation.repository

import com.lomeone.fnreservation.domain.reservation.entity.Reservation

interface ReservationRepository {
    suspend fun findByStoreBranchAndLatestGameType(storeBranch: String, gameType: String): Reservation?
    suspend fun insertOne(reservation: Reservation): Reservation
}
