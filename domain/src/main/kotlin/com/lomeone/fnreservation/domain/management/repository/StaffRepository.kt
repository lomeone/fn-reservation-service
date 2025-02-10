package com.lomeone.fnreservation.domain.management.repository

import com.lomeone.fnreservation.domain.management.entity.Staff

interface StaffRepository {
    fun findByStoreBranchAndName(storeBranch: String, name: String): Staff?
    fun save(staff: Staff): Staff
}
