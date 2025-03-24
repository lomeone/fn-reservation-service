package com.lomeone.fnreservation.application.rest.api.management

data class DeactivateStaffRequest(
    val storeBranch: String,
    val name: String
)

data class DeactivateStaffResponse(
    val storeBranch: String,
    val staffName: String,
    val status: String
)
