package com.example.cateat.service.common

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

object RestClient {
    /**
     * http-клиент для взаимодействия с сервисом.
     */
    val http = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }
        install(ContentNegotiation) {
            json()
        }
    }
}