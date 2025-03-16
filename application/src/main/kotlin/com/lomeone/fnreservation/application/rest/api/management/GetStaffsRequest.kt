package com.lomeone.fnreservation.application.rest.api.management

data class GetStaffsRequest(
    val storeBranch: String
)

data class GetStaffsResponse(
    val storeBranch: String,
    val staffs: List<String>
)
