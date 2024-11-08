val koin_version: String by project
val prometheus_version: String by project
val swagger_version: String by project

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
