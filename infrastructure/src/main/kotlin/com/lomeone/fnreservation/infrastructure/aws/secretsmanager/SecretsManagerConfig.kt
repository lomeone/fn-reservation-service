package com.lomeone.fnreservation.infrastructure.aws.secretsmanager

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueResponse
import kotlinx.coroutines.runBlocking

object SecretsManagerConfig {
    private val secretsClient: SecretsManagerClient by lazy {
        SecretsManagerClient { region = "ap-northeast-2" }  // ✅ 한 번만 생성하여 재사용
    }

    fun getSecretValue(valueRequest: GetSecretValueRequest): GetSecretValueResponse = runBlocking {
        secretsClient.getSecretValue(valueRequest)
    }

    fun close() {
        secretsClient.close()
    }
}
