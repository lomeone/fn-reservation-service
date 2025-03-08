package com.lomeone.fnreservation.application

import com.lomeone.fnreservation.application.rest.api.management.routeStaff
import com.lomeone.fnreservation.application.rest.api.reservation.routeReservation
import com.lomeone.fnreservation.application.plugins.configureMonitoring
import com.lomeone.fnreservation.application.plugins.configureRouting
import com.lomeone.fnreservation.application.plugins.configureSerialization
import com.lomeone.fnreservation.application.plugins.configureStatus
import com.lomeone.fnreservation.domain.management.repository.StaffRepository
import com.lomeone.fnreservation.domain.management.service.DeactivateStaffService
import com.lomeone.fnreservation.domain.management.service.GetStaffsService
import com.lomeone.fnreservation.domain.management.service.RegisterStaffService
import com.lomeone.fnreservation.domain.reservation.repository.ReservationRepository
import com.lomeone.fnreservation.domain.reservation.service.*
import com.lomeone.fnreservation.infrastructure.aws.secretsmanager.SecretsManagerConfig
import com.lomeone.fnreservation.infrastructure.database.mongodb.MongoConfig
import com.lomeone.fnreservation.infrastructure.management.repository.StaffRepositoryImpl
import com.lomeone.fnreservation.infrastructure.reservation.repository.ReservationRepositoryImpl
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
        .monitor.subscribe(ApplicationStopping) {
            MongoConfig.close()
            SecretsManagerConfig.close()
        }
}

fun Application.injectConfigure() {
    val infrastructureConfig = HoconApplicationConfig(ConfigFactory.load("infrastructure.conf"))

    install(Koin) {
        modules(org.koin.dsl.module {
            single {
                MongoConfig.getMongoDatabase(infrastructureConfig.property("ktor.mongo.database").getString())
            }
        }, org.koin.dsl.module {
            single<ReservationRepository> { ReservationRepositoryImpl(get()) }
            single<StaffRepository> { StaffRepositoryImpl(get()) }
        }, org.koin.dsl.module {
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
        }, org.koin.dsl.module {
            single {
                RegisterStaffService(get())
            }
            single {
                GetStaffsService(get())
            }
            single {
                DeactivateStaffService(get())
            }
        })
    }
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureRouting()
    configureStatus()
    injectConfigure()
    routeReservation()
    routeStaff()
}
