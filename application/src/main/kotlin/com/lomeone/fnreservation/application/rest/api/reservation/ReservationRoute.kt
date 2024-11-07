package com.lomeone.fnreservation.application.rest.api.reservation

import com.lomeone.com.lomeone.fnreservation.domain.reservation.service.ReserveCommand
import com.lomeone.com.lomeone.fnreservation.domain.reservation.service.ReserveService
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import com.lomeone.fnreservation.domain.reservation.service.GetReservationQuery
import com.lomeone.fnreservation.domain.reservation.service.GetReservationService
import com.lomeone.fnreservation.domain.reservation.service.StartReservationCommand
import com.lomeone.fnreservation.domain.reservation.service.StartReservationService
import com.lomeone.com.lomeone.fnreservation.infrastructure.reservation.repository.ReservationRepositoryImpl
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.typesafe.config.ConfigFactory
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun Application.routeReservation() {
    val infrastructureConfig = HoconApplicationConfig(ConfigFactory.load("infrastructure.conf"))
    install(Koin) {
        modules(module {
            single<MongoClient> {
                MongoClient.create(infrastructureConfig.property("ktor.mongo.uri").getString())
            }
            single {
                get<MongoClient>().getDatabase(infrastructureConfig.property("ktor.mongo.database").getString())
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
                ReserveService(get())
            }
        })
    }

    val startReservationService by inject<StartReservationService>()
    val getReservationService by inject<GetReservationService>()
    val reserveService by inject<ReserveService>()

    routing {
        get<GetReservation> { request ->
            println(request.gameType)
            val query = GetReservationQuery(request.storeBranch, request.gameType)
            val result = getReservationService.getReservation(query)
            call.respond(result)
        }
        post<ReservationStart> {
            val request = call.receive<ReservationStartRequest>()
            val command = StartReservationCommand(
                storeBranch = request.storeBranch,
                gameType = request.gameType,
                24102001
            )
            val result = startReservationService.startReservation(command)
            call.respond(result)
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
            call.respond(result)
        }
    }
}

@Serializable
@Resource("/reservation")
data class GetReservation(
    val storeBranch: String,
    val gameType: String
) {
}

@Resource("/reservation/start")
class ReservationStart

@Serializable
data class ReservationStartRequest(
    val storeBranch: String,
    val gameType: String
)

@Resource("/reservation")
class Reservation


@Serializable
data class ReservationRequest(
    val storeBranch: String,
    val gameType: String,
    val reservationUsers: Set<String>,
    val reservationTime: String
)