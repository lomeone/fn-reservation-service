package com.lomeone.fnreservation.domain.management.entity

import java.security.MessageDigest
import java.time.ZonedDateTime

class Staff(
    id: String? = null,
    val storeBranch: String,
    val name: String,
    var role: StaffRole = StaffRole.STAFF,
    var status: StaffStatus = StaffStatus.ACTIVE,
    val createdAt: ZonedDateTime = ZonedDateTime.now(),
    val updatedAt: ZonedDateTime = ZonedDateTime.now()
) {

    val id: String = id ?: MessageDigest.getInstance("SHA-256")
        .digest("${storeBranch}_${name}".toByteArray())
        .joinToString("") { "%02x".format(it) }

    fun deactivate() {
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
