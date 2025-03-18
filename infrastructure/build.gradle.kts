val awsSecretsmanagerJvmVersion: String by project

dependencies {
    implementation(project(":domain"))
    implementation(kotlin("reflect"))

    implementation("aws.sdk.kotlin:secretsmanager-jvm:$awsSecretsmanagerJvmVersion")
}
