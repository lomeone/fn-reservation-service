package com.lomeone.fnreservation.infrastructure.reservation.repository

import com.lomeone.fnreservation.domain.reservation.entity.Reservation
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import com.mongodb.MongoException
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.conversions.Bson
import org.slf4j.LoggerFactory
import kotlin.reflect.full.memberProperties

private const val RESERVATION_COLLECTION = "reservation"

class ReservationRepositoryImpl(
    private val mongoDatabase: MongoDatabase
) : ReservationRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun findByStoreBranchAndLatestGameType(storeBranch: String, gameType: String): Reservation? =
        runBlocking {
            mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION)
                .find(and(
                    eq(Reservation::storeBranch.name, storeBranch),
                    eq(Reservation::gameType.name, gameType),
                    gte(Reservation::session.name, 0)
                ))
                .sort(org.bson.Document("createdAt", -1))
                .firstOrNull()
        }

    override fun findByStoreBranchAndGameTypeAndSession(
        storeBranch: String,
        gameType: String,
        session: Int
    ): Reservation? = runBlocking {
        mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION)
            .find(and(
                eq(Reservation::storeBranch.name, storeBranch),
                eq(Reservation::gameType.name, gameType),
                eq(Reservation::session.name, session)
            ))
            .sort(org.bson.Document("createdAt", -1))
            .firstOrNull()
    }

    override fun save(reservation: Reservation): Reservation {
        try {
            return if (isAlreadyExistReservation(reservation)) {
                updateReservation(reservation)
            } else {
                insertReservation(reservation)
            }
        } catch (e: MongoException) {
            log.error("Unable to insert due to an error: $e")
            throw Exception()
        }
    }

    private fun isAlreadyExistReservation(reservation: Reservation) = runBlocking {
        mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION)
            .find(eq("_id", reservation.id))
            .sort(org.bson.Document("createdAt", -1))
            .firstOrNull() != null
    }

    private fun updateReservation(reservation: Reservation): Reservation {
        val query = eq("_id", reservation.id)
        val updateList = mutableListOf<Bson>()
        for (property in Reservation::class.memberProperties) {
            if (property.name != Reservation::id.name) {
                val value = property.get(reservation)
                if (value != null) {
                    updateList.add(Updates.set(property.name, value))
                }
            }
        }

        val updates = Updates.combine(updateList)

        val options = UpdateOptions().upsert(true)
        return runBlocking {
            mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION)
                .updateOne(query, updates, options).let { reservation }
        }
    }

    private fun insertReservation(reservation: Reservation): Reservation {

        val result = runBlocking {
            mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION).insertOne(reservation)
        }

        return runBlocking {
            mongoDatabase.getCollection<Reservation>(RESERVATION_COLLECTION)
                .withDocumentClass<Reservation>()
                .find(eq("_id", result.insertedId))
                .firstOrNull() ?: throw Exception()
        }
    }
}
