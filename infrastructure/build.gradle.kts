val aws_secretsmanager_jvm_version: String by project

dependencies {
    implementation(project(":domain"))
    implementation(kotlin("reflect"))

    implementation("aws.sdk.kotlin:secretsmanager-jvm:$aws_secretsmanager_jvm_version")
}
