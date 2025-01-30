package com.lomeone.fnreservation.application.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import com.lomeone.eunoia.exception.EunioaException
import com.lomeone.eunoia.exception.ExceptionCategory
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

fun Application.configureStatus() {
    install(StatusPages) {
        exception<EunioaException> { call, cause ->
            call.respond(
                status = convertErrorCategoryToHttpStatusCode(cause.errorCode.exceptionCategory),
                message = ExceptionResponse(
                    status = convertErrorCategoryToHttpStatusCode(cause.errorCode.exceptionCategory).value,
                    errorCode = cause.errorCode.code,
                    message = cause.message,
                    detail = cause.detail.details.toString()
                )
            )
        }
    }
}

private fun convertErrorCategoryToHttpStatusCode(exceptionCategory: ExceptionCategory): HttpStatusCode =
    when(exceptionCategory) {
        ExceptionCategory.BAD_REQUEST -> HttpStatusCode.BadRequest
        ExceptionCategory.UNAUTHORIZED -> HttpStatusCode.Unauthorized
        ExceptionCategory.FORBIDDEN -> HttpStatusCode.Forbidden
        ExceptionCategory.NOT_FOUND -> HttpStatusCode.NotFound
    }

@Serializable
data class ExceptionResponse(
    val timestamp: String = ZonedDateTime.now().toString(),
    val status: Int,
    val errorCode: String,
    val message: String? = null,
    val detail: String
)
