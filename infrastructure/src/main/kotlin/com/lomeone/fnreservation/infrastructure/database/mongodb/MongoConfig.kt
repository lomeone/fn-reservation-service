package com.lomeone.fnreservation.infrastructure.database.mongodb

import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.lomeone.fnreservation.infrastructure.aws.secretsmanager.SecretsManagerConfig
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

object MongoConfig {
    private val mongoClient: MongoClient by lazy {
        val uri = getMongodbUri()
        MongoClient.create(uri)
    }

    fun getMongoDatabase(database: String): MongoDatabase = mongoClient.getDatabase(database)

    private fun getMongodbUri(): String {
        val valueRequest =
            GetSecretValueRequest {
                secretId = "/prod/fn-reservation-service/mongodb"
            }

        val response = SecretsManagerConfig.getSecretValue(valueRequest)

        return response.secretString ?: throw Exception()
    }

    fun close() {
        mongoClient.close()
    }
}
