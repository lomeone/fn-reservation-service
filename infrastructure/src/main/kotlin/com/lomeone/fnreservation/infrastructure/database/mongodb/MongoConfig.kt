package com.lomeone.fnreservation.infrastructure.database.mongodb

import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.lomeone.fnreservation.infrastructure.aws.secretsmanager.SecretsManagerConfig
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoConfig {
    private val mongoClient: MongoClient by lazy {
        val uri = getMongodbUri()
        MongoClient.create(uri)
    }

    private fun getMongodbUri(): String {
        val valueRequest =
            GetSecretValueRequest {
                secretId = "/prod/fn-reservation-service/mongodb"
            }

        val response = SecretsManagerConfig.getSecretValue(valueRequest)

        return response.secretString ?: throw Exception()
    }

    @Bean
    fun getMongoDatabase(@Value("\${mongodb.database}") database: String): MongoDatabase = mongoClient.getDatabase(database)

    @PreDestroy
    fun close() {
        mongoClient.close()
    }
}
