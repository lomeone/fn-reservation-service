package com.lomeone.fnreservation.domain.reservation.exception

class ReservationNotFoundException(
    detail: Map<String, Any>
) : CustomException(
    message = MESSAGE,
    errorCode = ERROR_CODE,
    detail = ExceptionDetail(
        details = detail
    )
) {
    companion object {
        val ERROR_CODE = ErrorCode(
            code = "user/deletion-request-not-found",
            exceptionCategory = ExceptionCategory.NOT_FOUND
        )
        const val MESSAGE = "Deletion request not found"
    }
}
