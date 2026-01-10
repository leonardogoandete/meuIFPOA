package br.com.ifrs.meuifpoa.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// HttpClient pré-configurado para ser usado em toda a aplicação.
val ktorClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true // Ignora chaves desconhecidas no JSON
        })
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }

    defaultRequest {
        url("https://app.poa.ifrs.edu.br/meuifpoa/")
    }
}
