val koin_version: String by project

dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    implementation("io.insert-koin:koin-ktor:$koin_version")
}

application {
    mainClass.set("com.lomeone.application.ApplicationKt")

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

tasks.test {
    useJUnitPlatform()
}