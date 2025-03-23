package com.lomeone.fnreservation.application.rest.api.reservation

import com.lomeone.fnreservation.domain.reservation.service.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reservation")
class ReservationController(
    private val getReservationService: GetReservationService,
    private val startReservationService: StartReservationService,
    private val closeReservationService: CloseReservationService,
    private val reserveService: ReserveService,
    private val cancelReservationService: CancelReservationService
) {
    @GetMapping
    fun getReservation(request: GetReservationRequest): GetReservationResponse {
        val result = getReservationService.getReservation(GetReservationQuery(request.storeBranch, request.gameType))

        return GetReservationResponse(
            gameType = result.gameType,
            session = result.session,
            reservation = result.reservation,
            status = result.status.name
        )
    }

    @PostMapping("/start")
    fun startReservation(@RequestBody request: ReservationStartRequest): ReservationStartResponse {
        val result = startReservationService.startReservation(
            StartReservationCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType,
                session = request.session
            )
        )

        return ReservationStartResponse(
            storeBranch = result.storeBranch,
            gameType = result.gameType,
            session = result.session,
            reservation = result.reservation
        )
    }

    @PostMapping("/close")
    fun closeReservation(@RequestBody request: ReservationCloseRequest): ReservationCloseResponse {
        val result = closeReservationService.closeReservation(
            CloseReservationCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType
            )
        )

        return ReservationCloseResponse(
            storeBranch = result.storeBranch,
            gameType = result.gameType,
            session = result.session
        )
    }

    @PostMapping
    fun reserve(@RequestBody request: ReservationRequest): ReservationResponse {
        val result = reserveService.reserve(
            ReserveCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType,
                reservationUsers = request.reservationUsers.toSet(),
                reservationTime = request.reservationTime
            )
        )

        return ReservationResponse(
            gameType = result.gameType,
            session = result.session,
            reservation = result.reservation
        )
    }

    @PostMapping("/cancel")
    fun cancelReservation(@RequestBody request: CancelReservationRequest): CancelReservationResponse {
        val result = cancelReservationService.cancel(
            CancelReservationCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType,
                cancelUsers = request.cancelUsers
            )
        )

        return CancelReservationResponse(
            gameType = result.gameType,
            session = result.session,
            reservation = result.reservation
        )
    }
}
