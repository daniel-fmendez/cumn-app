package com.example.armoryboxkotline.Conection

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object Config {
    const val BASE_URL = "http://192.168.1.133:3000"
}

object HttpClientSingleton {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true})

        }
    }
}