import java.io.ByteArrayOutputStream

val group_name: String by project

val logback_version: String by project
val eunoia_exception_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jetbrains.kotlin.plugin.serialization")
}

allprojects {
    group = group_name
    version = getGitHash()

    apply {
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.plugin.serialization")
    }

    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/lomeone/eunoia")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
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
        implementation("org.springframework.boot:spring-boot-starter-validation")

        // MongoDB
        implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.1")

        implementation("ch.qos.logback:logback-classic:$logback_version")

        implementation("com.lomeone.eunoia:exception:$eunoia_exception_version")

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
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
