package com.lomeone.fnreservation.application.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import com.lomeone.eunoia.exception.EunioaException
import com.lomeone.eunoia.exception.ExceptionCategory

fun Application.configureStatus() {
    install(StatusPages) {
        exception<EunioaException> { call, cause ->  
            call.respondText(
                text = cause.message ?: "",
                status = when (cause.errorCode.exceptionCategory) {
                    ExceptionCategory.BAD_REQUEST -> HttpStatusCode.BadRequest
                    ExceptionCategory.UNAUTHORIZED -> HttpStatusCode.Unauthorized
                    ExceptionCategory.FORBIDDEN -> HttpStatusCode.Forbidden
                    ExceptionCategory.NOT_FOUND -> HttpStatusCode.NotFound
                }
            )
        }
    }
}
