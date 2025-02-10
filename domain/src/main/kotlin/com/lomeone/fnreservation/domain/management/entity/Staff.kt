package com.lomeone.fnreservation.domain.management.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Staff(
    @BsonId
    val id: ObjectId = ObjectId(),
    val storeBranch: String,
    val name: String,
    val role: StaffRole = StaffRole.STAFF,
    val status: StaffStatus = StaffStatus.ACTIVE
) {
}

enum class StaffRole {
    MANAGER,
    STAFF
}

enum class StaffStatus {
    ACTIVE,
    INACTIVE
}
