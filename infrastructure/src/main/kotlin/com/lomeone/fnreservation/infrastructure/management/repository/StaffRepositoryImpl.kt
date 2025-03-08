package com.lomeone.fnreservation.infrastructure.management.repository

import com.lomeone.fnreservation.domain.management.entity.Staff
import com.lomeone.fnreservation.domain.management.entity.StaffStatus
import com.lomeone.fnreservation.domain.management.repository.StaffRepository
import com.mongodb.MongoException
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.conversions.Bson
import kotlin.reflect.full.memberProperties

private const val STAFF_COLLECTION = "staff"

class StaffRepositoryImpl(
    private val mongoDatabase: MongoDatabase
) : StaffRepository {
    override fun save(staff: Staff): Staff {
        try {
            return if (isAlreadyExistStaff(staff)) {
                updateStaff(staff)
            } else {
                insertStaff(staff)
            }
        } catch (e: MongoException) {
            throw Exception()
        }
    }

    private fun isAlreadyExistStaff(staff: Staff) = runBlocking {
        mongoDatabase.getCollection<Staff>(STAFF_COLLECTION)
            .find(eq("_id", staff.id))
            .sort(org.bson.Document("createdAt", -1))
            .firstOrNull() != null
    }

    private fun updateStaff(staff: Staff): Staff {
        val query = eq("_id", staff.id)
        val updateList = mutableListOf<Bson>()
        for (property in Staff::class.memberProperties) {
            val value = property.get(staff)
            if (value != null) {
                updateList.add(Updates.set(property.name, value))
            }
        }

        val updates = Updates.combine(updateList)
        val options = UpdateOptions().upsert(true)
        return runBlocking {
            mongoDatabase.getCollection<Staff>(STAFF_COLLECTION)
                .updateOne(query, updates, options).let { staff }
        }
    }

    private fun insertStaff(staff: Staff): Staff {
        val result = runBlocking {
            mongoDatabase.getCollection<Staff>(STAFF_COLLECTION).insertOne(staff)
        }

        return runBlocking {
            mongoDatabase.getCollection<Staff>(STAFF_COLLECTION)
                .withDocumentClass<Staff>()
                .find(eq("_id", result.insertedId))
                .firstOrNull() ?: throw Exception()
        }
    }

    override fun findByStoreBranchAndStatus(storeBranch: String, status: StaffStatus): List<Staff> =
        runBlocking {
            mongoDatabase.getCollection<Staff>(STAFF_COLLECTION)
                .find(and(
                    eq(Staff::storeBranch.name, storeBranch),
                    eq(Staff::status.name, status)
                ))
                .toList()
        }

    override fun findByStoreBranchAndName(storeBranch: String, name: String): Staff? =
        runBlocking {
            mongoDatabase.getCollection<Staff>(STAFF_COLLECTION)
                .find(and(
                    eq(Staff::storeBranch.name, storeBranch),
                    eq((Staff::name.name), name)
                )).firstOrNull()
        }
}
