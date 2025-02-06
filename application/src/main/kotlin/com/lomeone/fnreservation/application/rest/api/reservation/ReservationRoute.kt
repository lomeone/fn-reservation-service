package com.lomeone.fnreservation.application.rest.api.reservation

import com.lomeone.fnreservation.domain.reservation.service.ReserveCommand
import com.lomeone.fnreservation.domain.reservation.service.ReserveService
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import com.lomeone.fnreservation.infrastructure.reservation.repository.ReservationRepositoryImpl
import com.lomeone.fnreservation.domain.reservation.service.*
import com.lomeone.fnreservation.infrastructure.database.mongodb.MongoConfig
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun Application.routeReservation() {
    val infrastructureConfig = HoconApplicationConfig(ConfigFactory.load("infrastructure.conf"))
    install(Koin) {
        modules(module {
            single {
                MongoConfig.getMongoDatabase(infrastructureConfig.property("ktor.mongo.database").getString())
            }
        }, module {
            single<ReservationRepository> { ReservationRepositoryImpl(get()) }
        }, module {
            single {
                StartReservationService(get())
            }
            single {
                GetReservationService(get())
            }
            single {
                CloseReservationService(get())
            }
            single {
                ReserveService(get())
            }
            single {
                CancelReservationService(get())
            }
        })
    }

    val startReservationService by inject<StartReservationService>()
    val getReservationService by inject<GetReservationService>()
    val closeReservationService by inject<CloseReservationService>()
    val reserveService by inject<ReserveService>()
    val cancelReservationService by inject<CancelReservationService>()

    routing {
        get<GetReservation> { request ->
            val query = GetReservationQuery(request.storeBranch, request.gameType)
            val result = getReservationService.getReservation(query)

            call.respond(GetReservationResponse(
                gameType = result.gameType,
                session = result.session,
                reservation = result.reservation,
                status = result.status.name
            ))
        }
        post<ReservationStart> {
            val request = call.receive<ReservationStartRequest>()

            val command = StartReservationCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType,
                session = request.session
            )

            val result = startReservationService.startReservation(command)

            call.respond(ReservationStartResponse(
                storeBranch = result.storeBranch,
                gameType = result.gameType,
                session = result.session,
                reservation = result.reservation
            ))
        }
        post<ReservationClose> {
            val request = call.receive<ReservationCloseRequest>()

            val command = CloseReservationCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType
            )

            val result = closeReservationService.closeReservation(command)

            call.respond(ReservationCloseResponse(
                storeBranch = result.storeBranch,
                gameType = result.gameType,
                session = result.session
            ))
        }
        post<Reservation> {
            val request = call.receive<ReservationRequest>()
            val command = ReserveCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType,
                reservationUsers = request.reservationUsers,
                reservationTime = request.reservationTime
            )
            val result = reserveService.reserve(command)

            call.respond(ReservationResponse(
                gameType = result.gameType,
                session = result.session,
                reservation = result.reservation
            ))
        }
        post<CancelReservation> {
            val request = call.receive<CancelReservationRequest>()
            val command = CancelReservationCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType,
                cancelUsers = request.cancelUsers
            )
            val result = cancelReservationService.cancel(command)

            call.respond(CancelReservationResponse(
                gameType = result.gameType,
                session = result.session,
                reservation = result.reservation
            ))
        }
    }
}
