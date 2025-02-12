package com.lomeone.fnreservation.domain.management.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Staff(
    @BsonId
    val id: ObjectId = ObjectId(),
    val storeBranch: String,
    val name: String,
    var role: StaffRole = StaffRole.STAFF,
    var status: StaffStatus = StaffStatus.ACTIVE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun inactivate() {
        this.status = StaffStatus.INACTIVE
    }

    fun activate() {
        this.status = StaffStatus.ACTIVE
    }
}

enum class StaffRole {
    MANAGER,
    STAFF
}

enum class StaffStatus {
    ACTIVE,
    INACTIVE
}
