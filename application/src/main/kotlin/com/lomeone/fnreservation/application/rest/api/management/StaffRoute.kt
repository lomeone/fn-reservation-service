package com.lomeone.fnreservation.application.rest.api.management

import com.lomeone.fnreservation.domain.management.repository.StaffRepository
import com.lomeone.fnreservation.domain.management.service.RegisterStaffCommand
import com.lomeone.fnreservation.domain.management.service.RegisterStaffService
import com.lomeone.fnreservation.infrastructure.database.mongodb.MongoConfig
import com.lomeone.fnreservation.infrastructure.management.repository.StaffRepositoryImpl
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun Application.routeStaff() {
    val registerStaffService by inject<RegisterStaffService>()

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
    }
}
