package com.lomeone.fnreservation.application.rest.api.management

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/staff")
class RegisterStaff

@Serializable
data class RegisterStaffRequest(
    val storeBranch: String,
    val name: String
)

@Serializable
data class RegisterStaffResponse(
    val storeBranch: String,
    val name: String,
    val role: String
)
