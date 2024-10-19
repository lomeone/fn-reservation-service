package com.lomeone.com.lomeone.infrastructure.reservation.repository

import com.lomeone.com.lomeone.domain.reservation.entity.Reservation
import com.lomeone.com.lomeone.domain.reservation.repository.ReservationRepository
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonValue
import org.slf4j.LoggerFactory

const val RESERVATION_COLLECTION = "reservation"

class ReservationRepositoryImpl(
    private val mongoDatabase: MongoDatabase
) : ReservationRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)
    override suspend fun findByLatestGameType(gameType: String): Reservation? {
        TODO("Not yet implemented")
    }

    override suspend fun insertOne(reservation: Reservation): Reservation {
        try {
            val result = mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).insertOne(reservation)

            return mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).withDocumentClass<Reservation>()
            .find(Filters.eq("_id", result.insertedId))
            .firstOrNull() ?: throw Exception()
        } catch (e: MongoException) {
            log.error("Unable to insert due to an error: $e")
            throw Exception()
        }
    }
}
