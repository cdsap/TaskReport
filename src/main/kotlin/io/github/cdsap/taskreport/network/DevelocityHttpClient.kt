package io.github.cdsap.taskreport.network

import io.github.cdsap.geapi.client.network.ClientConf
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson

internal fun develocityHttpClient(
    apiKey: String,
    clientConf: ClientConf,
    engine: HttpClientEngine = CIO.create { requestTimeout = 0 },
): HttpClient =
    HttpClient(engine) {
        expectSuccess = false
        install(ContentNegotiation) {
            gson()
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = clientConf.maxRetries)
            exponentialDelay(
                base = clientConf.exponentialBase,
                maxDelayMs = clientConf.exponentialMaxDelay,
            )
        }
        defaultRequest {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
        }
    }
