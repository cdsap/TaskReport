package io.github.cdsap.taskreport.network

import io.github.cdsap.geapi.client.network.ClientConf
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class DevelocityHttpClientTest {
    @Test
    fun `attaches bearer token without waiting for an auth challenge`() =
        runBlocking {
            var authorization: String? = null
            val engine =
                MockEngine { request ->
                    authorization = request.headers[HttpHeaders.Authorization]
                    respond(
                        content = "[]",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            val client =
                develocityHttpClient(
                    apiKey = "test-token",
                    clientConf = ClientConf(maxRetries = 0),
                    engine = engine,
                )

            client.get("https://ge.example/api/builds")
            client.close()

            assertEquals("Bearer test-token", authorization)
        }
}