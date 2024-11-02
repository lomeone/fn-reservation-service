package com.lomeone.com.lomeone.domain.reservation.repository

import com.lomeone.com.lomeone.domain.reservation.entity.Reservation
import org.bson.BsonValue

interface ReservationRepository {
    suspend fun findByStoreBranchAndLatestGameType(storeBranch: String, gameType: String): Reservation?
    suspend fun insertOne(reservation: Reservation): Reservation
}
