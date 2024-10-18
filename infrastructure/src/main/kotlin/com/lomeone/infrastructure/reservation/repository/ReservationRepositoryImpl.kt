package com.lomeone.com.lomeone.infrastructure.reservation.repository

import com.lomeone.com.lomeone.domain.reservation.entity.Reservation
import com.lomeone.com.lomeone.domain.reservation.repository.ReservationRepository
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.BsonValue
import org.slf4j.LoggerFactory

const val RESERVATION_COLLECTION = "reservation"

class ReservationRepositoryImpl(
    private val mongoDatabase: MongoDatabase
) : ReservationRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override suspend fun insertOne(reservation: Reservation): BsonValue? {
        try {
            val result = mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).insertOne(reservation)
            return result.insertedId
        } catch (e: MongoException) {
            log.error("Unable to insert due to an error: $e")
        }
        return null
    }
}