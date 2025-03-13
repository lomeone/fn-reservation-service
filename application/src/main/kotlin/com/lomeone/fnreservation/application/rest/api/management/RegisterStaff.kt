package com.lomeone.fnreservation.application.rest.api.management

data class RegisterStaffRequest(
    val storeBranch: String,
    val name: String
)

data class RegisterStaffResponse(
    val storeBranch: String,
    val name: String,
    val role: String
)
