package com.lomeone.fnreservation.domain.management.service

import com.lomeone.fnreservation.domain.management.entity.StaffStatus
import com.lomeone.fnreservation.domain.management.repository.StaffRepository
import org.springframework.stereotype.Service

@Service
class GetStaffsService(
    private val staffRepository: StaffRepository
) {
    fun getStaffs(query: GetStaffsQuery): GetStaffsResult {
        val staffs = staffRepository.findByStoreBranchAndStatus(query.storeBranch, StaffStatus.ACTIVE)

        return GetStaffsResult(
            storeBranch = query.storeBranch,
            staffs = staffs.map { it.name }
        )
    }
}

data class GetStaffsQuery(
    val storeBranch: String
)

data class GetStaffsResult(
    val storeBranch: String,
    val staffs: List<String>
)
