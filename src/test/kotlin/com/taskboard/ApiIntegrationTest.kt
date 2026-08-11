package com.taskboard

import com.taskboard.dto.HealthResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiIntegrationTest {
    @Test
    fun `health reports database UP`() = testApplication {
        environment {
            config = MapApplicationConfig(
                "db.url" to (System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/taskboard"),
                "db.user" to (System.getenv("DATABASE_USER") ?: "taskboard"),
                "db.password" to (System.getenv("DATABASE_PASSWORD") ?: "taskboard"),
                "db.maxPoolSize" to "5",
            )
        }
        application { module() }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<HealthResponse>()
        assertEquals("UP", body.status)
        assertEquals("UP", body.database)
    }
}
