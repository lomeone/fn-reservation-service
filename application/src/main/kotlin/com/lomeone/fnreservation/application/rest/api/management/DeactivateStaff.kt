package com.lomeone.fnreservation.application.rest.api.management

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/staff/deactivate")
class DeactivateStaff

@Serializable
data class DeactivateStaffRequest(
    val storeBranch: String,
    val name: String
)

@Serializable
data class DeactivateStaffResponse(
    val storeBranch: String,
    val staffName: String,
    val status: String
)
