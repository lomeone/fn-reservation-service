package com.lomeone.fnreservation.domain.management.repository

import com.lomeone.fnreservation.domain.management.entity.Staff
import com.lomeone.fnreservation.domain.management.entity.StaffStatus

interface StaffRepository {
    fun save(staff: Staff): Staff
    fun findById(id: String): Staff?
    fun findByStoreBranchAndStatus(storeBranch: String, status: StaffStatus): List<Staff>
    fun findByStoreBranchAndName(storeBranch: String, name: String): Staff?
}
