package com.lomeone.fnreservation.domain.reservation.exception

import com.lomeone.eunoia.exception.ErrorCode
import com.lomeone.eunoia.exception.EunioaException
import com.lomeone.eunoia.exception.ExceptionCategory
import com.lomeone.eunoia.exception.ExceptionDetail

private const val MESSAGE = "Reservation in progress"
private val ERROR_CODE = ErrorCode(
    code = "reservation/in-progress",
    exceptionCategory = ExceptionCategory.BAD_REQUEST
)

class ReservationInProgressException(
    detail: Map<String, Any>
) : EunioaException(
    message = MESSAGE,
    errorCode = ERROR_CODE,
    detail = ExceptionDetail(
        details = detail
    )
)
