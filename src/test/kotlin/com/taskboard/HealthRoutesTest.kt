package com.taskboard

import com.taskboard.dto.HealthResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthRoutesTest {
    @Test
    fun `GET health returns UP`() = testApplication {
        application {
            module()
        }

        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = jsonClient.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<HealthResponse>()
        assertEquals("UP", body.status)
        assertEquals("taskboard-api", body.service)
    }
}
