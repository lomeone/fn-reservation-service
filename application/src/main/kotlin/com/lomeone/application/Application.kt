package com.lomeone.application

import com.lomeone.application.plugins.*
import com.lomeone.application.rest.api.reservation.routeReservation
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureRouting()
    routeReservation()
}
