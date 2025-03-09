package com.lomeone.fnreservation.domain.management.service

import com.lomeone.fnreservation.domain.management.exception.StaffNotFoundException
import com.lomeone.fnreservation.domain.management.entity.Staff
import com.lomeone.fnreservation.domain.management.repository.StaffRepository

class DeactivateStaffService(
    private val staffRepository: StaffRepository
) {
    fun deactivateStaff(command: DeactivateStaffCommand): DeactivateStaffResult {
        val staff = getStaff(command)
        staff.deactivate()
        val savedStaff = staffRepository.save(staff)

        return DeactivateStaffResult(
            storeBranch = savedStaff.storeBranch,
            staffName = savedStaff.name,
            status = savedStaff.status.name
        )
    }

    private fun getStaff(command: DeactivateStaffCommand): Staff =
        staffRepository.findByStoreBranchAndName(command.storeBranch, command.staffName)
            ?: throw StaffNotFoundException(
                detail = mapOf(
                    "storeBranch" to command.storeBranch,
                    "name" to command.staffName
                )
            )
}

data class DeactivateStaffCommand(
    val storeBranch: String,
    val staffName: String
)

data class DeactivateStaffResult(
    val storeBranch: String,
    val staffName: String,
    val status: String
)
