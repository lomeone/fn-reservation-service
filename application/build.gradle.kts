import java.io.ByteArrayOutputStream

val micrometerRegistryVersion: String by project
val micrometerTracingVersion: String by project
val opentelemetryVersion: String by project
val opentelemetryInstrumentationVersion: String by project
val eunoiaSpringWebRestVersion: String by project

val image_registry: String by project
val service_name: String by project

plugins {
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow") {
        exclude(module = "undertow-websockets-jsr")
    }

    // Observability
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerRegistryVersion")
    implementation(platform("io.micrometer:micrometer-tracing-bom:$micrometerTracingVersion"))
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:$opentelemetryVersion")
//    implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:$opentelemetryInstrumentationVersion"))
//    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")

    implementation("com.lomeone.eunoia:spring-web-rest:$eunoiaSpringWebRestVersion")
}

jib {
    container {
        jvmFlags = listOf(
            "-XX:+UseContainerSupport",     // 컨테이너 환경 인식
            "-XX:MaxRAMPercentage=75.0",    // 힙 메모리 자동 조정
            "-XX:+UseG1GC"                  // G1 GC 사용 (컨테이너에 적합)
        )
    }
    from {
        image = "amazoncorretto:21-alpine3.21"
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
    tags.add(version.toString())

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
