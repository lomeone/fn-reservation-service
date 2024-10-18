val service_name: String by settings
rootProject.name = service_name

pluginManagement {
    val kotlin_version: String by settings
    val ktor_version: String by settings

    plugins {
        kotlin("jvm") version kotlin_version
        id("io.ktor.plugin") version ktor_version
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
    }
}

include("domain")
include("infrastructure")
include("application")
