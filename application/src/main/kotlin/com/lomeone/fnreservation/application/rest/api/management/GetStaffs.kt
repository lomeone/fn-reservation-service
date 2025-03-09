package com.lomeone.fnreservation.application.rest.api.management

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Resource("/staff")
data class GetStaffs(
    val storeBranch: String
)

@Serializable
data class GetStaffsResponse(
    val storeBranch: String,
    val staffs: List<String>
)
