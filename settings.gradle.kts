val service_name: String by settings
rootProject.name = service_name

pluginManagement {
    val kotlin_version: String by settings
    val springBootVersion: String by settings
    val springDependencyManagement: String by settings
    val jibVersion: String by settings
    val koverVersion: String by settings

    plugins {
        kotlin("jvm") version kotlin_version
        kotlin("plugin.spring") version kotlin_version
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagement
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
        id("com.google.cloud.tools.jib") version jibVersion
        id("org.jetbrains.kotlinx.kover") version koverVersion
    }
}

include("domain")
include("infrastructure")
include("application")
