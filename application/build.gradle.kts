import java.io.ByteArrayOutputStream

val koin_version: String by project
val prometheus_version: String by project
val swagger_version: String by project

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
