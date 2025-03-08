package com.lomeone.fnreservation.domain.management.service

import com.lomeone.fnreservation.domain.management.exception.AlreadyExistStaffException
import com.lomeone.fnreservation.domain.management.entity.Staff
import com.lomeone.fnreservation.domain.management.repository.StaffRepository

class RegisterStaffService(
    private val staffRepository: StaffRepository
) {
    fun registerStaff(command: RegisterStaffCommand): RegisterStaffResult {
        ensureUniqueStaff(command.storeBranch, command.name)

        val staff = staffRepository.save(
            Staff(
                storeBranch = command.storeBranch,
                name = command.name
            )
        )

        return RegisterStaffResult(
            storeBranch = staff.storeBranch,
            name = staff.name,
            role = staff.role.name
        )
    }

    private fun ensureUniqueStaff(storeBranch: String, name: String) {
        staffRepository.findByStoreBranchAndName(storeBranch, name) != null &&
                throw AlreadyExistStaffException(
                    detail = mapOf(
                        "storeBranch" to storeBranch,
                        "name" to name
                    )
                )
    }
}

data class RegisterStaffCommand(
    val storeBranch: String,
    val name: String
)

data class RegisterStaffResult(
    val storeBranch: String,
    val name: String,
    val role: String
)
