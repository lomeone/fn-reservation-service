import java.io.ByteArrayOutputStream

val koin_version: String by project
val prometheus_version: String by project
val swagger_version: String by project

val image_registry: String by project
val service_name: String by project

plugins {
    id("io.ktor.plugin")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    // ktor
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-resources-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheus_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-status-pages")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
}

application {
    mainClass.set("com.lomeone.fnreservation.application.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.register<Copy>("mergeConfigs") {
    from(project(":domain").file("src/main/resources/domain.conf"))
    from(project(":infrastructure").file("src/main/resources/infrastructure.conf"))
    into(layout.buildDirectory.dir("resources/main"))
}

tasks.named("processResources") {
    dependsOn("mergeConfigs")
}

jib {
    from {
        image = "amazoncorretto:21"
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "$image_registry/$service_name"
        tags = getImageTags()
    }
}

fun getImageTags(): Set<String> {
    val tags = mutableSetOf<String>()
    val branch = getGitCurrentBranch()

    if (branch.isNotBlank()) {
        tags.add(branch)
    }
    tags.add(getGitHash())

    return tags
}

fun getGitCurrentBranch(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim().replace("/","-")
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}
