package com.lomeone.fnreservation.application.rest.api.management

import com.lomeone.fnreservation.domain.management.service.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/staff")
class StaffController(
    private val registerStaffService: RegisterStaffService,
    private val getStaffsService: GetStaffsService,
    private val deactivateStaffService: DeactivateStaffService
) {
    @PostMapping
    fun registerStaff(request: RegisterStaffRequest): RegisterStaffResponse {
        val result = registerStaffService.registerStaff(RegisterStaffCommand(request.storeBranch, request.name))

        return RegisterStaffResponse(
            storeBranch = result.storeBranch,
            name = result.name,
            role = result.role
        )
    }

    @GetMapping
    fun getStaffs(request: GetStaffsRequest): GetStaffsResponse {
        val result = getStaffsService.getStaffs(GetStaffsQuery(request.storeBranch))

        return GetStaffsResponse(
            storeBranch = result.storeBranch,
            staffs = result.staffs
        )
    }

    @PostMapping("/deactivate")
    fun deactivateStaff(request: DeactivateStaffRequest): DeactivateStaffResponse {
        val result = deactivateStaffService.deactivateStaff(DeactivateStaffCommand(request.storeBranch, request.name))

        return DeactivateStaffResponse(
            storeBranch = result.storeBranch,
            staffName = result.staffName,
            status = result.status
        )
    }
}
