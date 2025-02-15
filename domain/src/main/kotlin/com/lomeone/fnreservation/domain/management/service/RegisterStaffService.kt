package com.lomeone.fnreservation.domain.management.service

import com.lomeone.fnreservation.domain.management.entity.Staff
import com.lomeone.fnreservation.domain.management.repository.StaffRepository

class RegisterStaffService(
    private val staffRepository: StaffRepository
) {
    fun registerStaff(command: RegisterStaffCommand): RegisterStaffResult {
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
