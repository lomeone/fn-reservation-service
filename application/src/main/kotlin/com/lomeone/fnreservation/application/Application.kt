package com.lomeone.fnreservation.application

import com.lomeone.fnreservation.application.rest.api.reservation.routeReservation
import com.lomeone.fnreservation.application.plugins.configureMonitoring
import com.lomeone.fnreservation.application.plugins.configureRouting
import com.lomeone.fnreservation.application.plugins.configureSerialization
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
