import java.io.ByteArrayOutputStream

val group_name: String by project

val logback_version: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

allprojects {
    group = group_name
    version = getGitHash()

    apply {
        plugin("kotlin")
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
        // MongoDB
        implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")

        implementation("ch.qos.logback:logback-classic:$logback_version")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    }

    tasks.test {
        useJUnitPlatform()
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