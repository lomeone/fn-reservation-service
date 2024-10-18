import java.io.ByteArrayOutputStream

val group_name: String by project

val logback_version: String by project
val prometheus_version: String by project
val swagger_version: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

allprojects {
    group = group_name
    version = getGitHash()

    apply {
        plugin("kotlin")
        plugin("io.ktor.plugin")
        plugin("org.jetbrains.kotlin.plugin.serialization")
    }

    repositories {
        mavenCentral()
    }
}

subprojects {
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    kotlin {
        jvmToolchain(21)
    }

    dependencies {
        // ktor
        implementation("io.ktor:ktor-server-core-jvm")
        implementation("io.ktor:ktor-server-resources-jvm")
        implementation("io.ktor:ktor-server-call-logging-jvm")

        // MongoDB
        implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")

        implementation("io.ktor:ktor-server-metrics-micrometer-jvm")
        implementation("io.micrometer:micrometer-registry-prometheus:$prometheus_version")
        implementation("io.ktor:ktor-server-content-negotiation-jvm")
        implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
        implementation("io.ktor:ktor-server-netty-jvm")
        implementation("ch.qos.logback:logback-classic:$logback_version")
        testImplementation("io.ktor:ktor-server-test-host-jvm")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    }
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}