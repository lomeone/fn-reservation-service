package com.lomeone.fnreservation.domain.reservation.exception

import com.lomeone.eunoia.exception.ErrorCode
import com.lomeone.eunoia.exception.EunioaException
import com.lomeone.eunoia.exception.ExceptionCategory
import com.lomeone.eunoia.exception.ExceptionDetail

private val ERROR_CODE = ErrorCode(
    code = "reservation/not-found",
    exceptionCategory = ExceptionCategory.NOT_FOUND
)
private const val MESSAGE = "Reservation not found"

class ReservationNotFoundException(
    detail: Map<String, Any>
) : EunioaException(
    message = MESSAGE,
    errorCode = ERROR_CODE,
    detail = ExceptionDetail(
        details = detail
    )
)
