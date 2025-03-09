package com.lomeone.fnreservation.domain.management.exception

import com.lomeone.eunoia.exception.ErrorCode
import com.lomeone.eunoia.exception.EunioaException
import com.lomeone.eunoia.exception.ExceptionCategory
import com.lomeone.eunoia.exception.ExceptionDetail

private val MESSAGE = "Staff not found"
private val ERROR_CODE = ErrorCode(
    code = "staff/not-found",
    exceptionCategory = ExceptionCategory.NOT_FOUND
)

class StaffNotFoundException(
    detail: Map<String, Any>
) : EunioaException(
    message = MESSAGE,
    errorCode = ERROR_CODE,
    detail = ExceptionDetail(
        details = detail
    )
)
