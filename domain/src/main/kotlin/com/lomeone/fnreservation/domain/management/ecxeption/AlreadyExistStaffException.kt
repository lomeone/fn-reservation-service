package com.lomeone.fnreservation.domain.management.ecxeption

import com.lomeone.eunoia.exception.ErrorCode
import com.lomeone.eunoia.exception.EunioaException
import com.lomeone.eunoia.exception.ExceptionCategory
import com.lomeone.eunoia.exception.ExceptionDetail

private const val MESSAGE = "Already exist staff"
private val ERROR_CODE = ErrorCode(
    code = "management/already-exist-staff",
    exceptionCategory = ExceptionCategory.BAD_REQUEST
)

class AlreadyExistStaffException(
    detail: Map<String, Any>
) : EunioaException(
    message = MESSAGE,
    errorCode = ERROR_CODE,
    detail = ExceptionDetail(
        details = detail
    )
)
