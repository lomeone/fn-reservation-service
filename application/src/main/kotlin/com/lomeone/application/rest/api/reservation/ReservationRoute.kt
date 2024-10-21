package com.lomeone.application.rest.api.reservation

import com.lomeone.com.lomeone.domain.reservation.repository.ReservationRepository
import com.lomeone.com.lomeone.domain.reservation.service.GetReservationQuery
import com.lomeone.com.lomeone.domain.reservation.service.GetReservationService
import com.lomeone.com.lomeone.domain.reservation.service.StartReservationCommand
import com.lomeone.com.lomeone.domain.reservation.service.StartReservationService
import com.lomeone.com.lomeone.infrastructure.reservation.repository.ReservationRepositoryImpl
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
            single {
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
        })
    }

    val getReservationService by inject<GetReservationService>()
    val startReservationService by inject<StartReservationService>()

    routing {
        get<Reservation> { request ->
            println(request.gameType)
            val query = GetReservationQuery(request.gameType ?: throw Exception())
            val result = getReservationService.getReservation(query)
            call.respond(result)
        }
        post<ReservationStart> {
            val request = call.receive<ReservationRequest>()
            println("test $request")
            val command = StartReservationCommand(
                gameType = request.gameType,
                24102001
            )
            val result = startReservationService.startReservation(command)
            call.respond(result)
        }
    }
}

@Serializable
@Resource("/reservation")
data class Reservation(val gameType: String? = null) {
}

@Resource("/reservation/start")
class ReservationStart

@Serializable
data class ReservationRequest(
    val gameType: String
)