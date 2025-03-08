package com.lomeone.fnreservation.application.rest.api.management

import com.lomeone.fnreservation.domain.management.service.GetStaffsQuery
import com.lomeone.fnreservation.domain.management.service.GetStaffsService
import com.lomeone.fnreservation.domain.management.service.RegisterStaffCommand
import com.lomeone.fnreservation.domain.management.service.RegisterStaffService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.routeStaff() {
    val registerStaffService by inject<RegisterStaffService>()
    val getStaffsService by inject<GetStaffsService>()

    routing {
        post<RegisterStaff> {
            val request = call.receive<RegisterStaffRequest>()

            val command = RegisterStaffCommand(request.storeBranch, request.name)
            val result = registerStaffService.registerStaff(command)

            call.respond(
                RegisterStaffResponse(
                    storeBranch = result.storeBranch,
                    name = result.name,
                    role = result.role
                )
            )
        }
        get<GetStaffs> { request ->
            val query = GetStaffsQuery(request.storeBranch)
            val result = getStaffsService.getStaffs(query)

            call.respond(GetStaffsResponse(
                storeBranch = result.storeBranch,
                staffs = result.staffs
            ))
        }
    }
}
