package com.lomeone.com.lomeone.fnreservation.infrastructure.reservation.repository

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import com.mongodb.MongoException
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson
import org.slf4j.LoggerFactory
import kotlin.reflect.full.memberProperties

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
            if (isAlreadyExistReservation(reservation)) {
                val query = eq("_id", reservation.id)
                val updateList = mutableListOf<Bson>()
                for(property in Reservation::class.memberProperties) {
                    if (property.name != Reservation::id.name) {
                        val value = property.get(reservation)
                        if (value != null) {
                            updateList.add(Updates.set(property.name, value))
                        }
                    }
                }

                val updates = Updates.combine(updateList)

                val options = UpdateOptions().upsert(true)
                return mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).updateOne(query, updates, options).let { reservation }
            } else {
                val result = mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).insertOne(reservation)

                return mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).withDocumentClass<Reservation>()
                    .find(eq("_id", result.insertedId))
                    .firstOrNull() ?: throw Exception()
            }
        } catch (e: MongoException) {
            log.error("Unable to insert due to an error: $e")
            throw Exception()
        }
    }

    private suspend fun isAlreadyExistReservation(reservation: Reservation) = mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION)
        .find(eq("_id", reservation.id))
        .sort(org.bson.Document("createdAt", -1))
        .firstOrNull() != null
}
