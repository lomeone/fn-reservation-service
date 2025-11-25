val awsStsVersion: String by project
val awsDynamoDbVersion: String by project
val opentelemetryInstrumentationAwsSdkVerVersion: String by project

dependencies {
    implementation(project(":domain"))
    implementation(kotlin("reflect"))

    implementation("software.amazon.awssdk:sts:$awsStsVersion")
    implementation("software.amazon.awssdk:dynamodb:$awsDynamoDbVersion")
    implementation("software.amazon.awssdk:dynamodb-enhanced:$awsDynamoDbVersion")

    // Observability
    implementation("io.opentelemetry.instrumentation:opentelemetry-aws-sdk-2.2:${opentelemetryInstrumentationAwsSdkVerVersion}")
}
