dependencies {
    implementation(project(":domain"))
    implementation(kotlin("reflect"))

    implementation("aws.sdk.kotlin:secretsmanager-jvm:1.4.6")
}
