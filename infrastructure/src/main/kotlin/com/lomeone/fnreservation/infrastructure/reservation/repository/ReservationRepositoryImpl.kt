package com.lomeone.com.lomeone.fnreservation.infrastructure.reservation.repository

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import com.mongodb.MongoException
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.slf4j.LoggerFactory

const val RESERVATION_COLLECTION = "reservation"

class ReservationRepositoryImpl(
    private val mongoDatabase: MongoDatabase
) : ReservationRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)
    override suspend fun findByStoreBranchAndLatestGameType(storeBranch: String, gameType: String): Reservation? {
        val result = mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION)
            .find(and(eq(Reservation::storeBranch.name, storeBranch), eq(Reservation::gameType.name, gameType)))
            .sort(org.bson.Document("createdAt", -1))
            .firstOrNull()

        return result
    }

    override suspend fun save(reservation: Reservation): Reservation {
        try {
            val result = mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).insertOne(reservation)

            return mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).withDocumentClass<Reservation>()
            .find(eq("_id", result.insertedId))
            .firstOrNull() ?: throw Exception()
        } catch (e: MongoException) {
            log.error("Unable to insert due to an error: $e")
            throw Exception()
        }
    }
}
