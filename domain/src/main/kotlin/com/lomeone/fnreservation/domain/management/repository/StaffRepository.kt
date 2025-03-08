package com.lomeone.fnreservation.domain.management.repository

import com.lomeone.fnreservation.domain.management.entity.Staff

interface StaffRepository {
    fun save(staff: Staff): Staff
    fun findByStoreBranch(storeBranch: String): List<Staff>
    fun findByStoreBranchAndName(storeBranch: String, name: String): Staff?
}
