package com.lomeone.fnreservation.infrastructure.reservation.exception

import com.lomeone.eunoia.exception.ErrorCode
import com.lomeone.eunoia.exception.EunioaException
import com.lomeone.eunoia.exception.ExceptionCategory
import com.lomeone.eunoia.exception.ExceptionDetail

private const val MESSAGE = "Reservation put item not found"
private val ERROR_CODE = ErrorCode(
    code = "dynamo/reservation-put-item-not-found",
    exceptionCategory = ExceptionCategory.INTERNAL_SERVER_ERROR
)

class DynamoReservationPutItemNotFoundException(
    detail: Map<String, Any>
) : EunioaException(
    message = MESSAGE,
    errorCode = ERROR_CODE,
    detail = ExceptionDetail(
        details = detail
    )
)
