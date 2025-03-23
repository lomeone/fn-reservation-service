import java.io.ByteArrayOutputStream

val group_name: String by project

val mongodbDriverVersion: String by project

val logback_version: String by project
val eunoiaExceptionVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.cloud.tools.jib")
    id("org.jetbrains.kotlinx.kover")
    id("org.sonarqube")
    id("com.github.kt3k.coveralls")
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
        plugin("com.google.cloud.tools.jib")
        plugin("org.jetbrains.kotlinx.kover")
    }

    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/lomeone/eunoia")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.koverVerify, tasks.koverHtmlReport, tasks.koverXmlReport)
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

        implementation("ch.qos.logback:logback-classic:$logback_version")

        implementation("com.lomeone.eunoia:exception:$eunoiaExceptionVersion")

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }
}

dependencies {
    kover(project(":application"))
    kover(project(":domain"))
    kover(project(":infrastructure"))
}

sonarqube {
    properties {
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.file("reports/kover/report.xml")}")
    }
}

coveralls {
    jacocoReportPath = "${projectDir}/build/reports/kover/report.xml"
    sourceDirs = subprojects.map { it.sourceSets.main.get().allSource.srcDirs.toList() }
        .toList().flatten().map { relativePath(it) }
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}
