package com.taskboard

import com.taskboard.dto.AuthResponse
import com.taskboard.dto.HealthResponse
import com.taskboard.dto.LoginRequest
import com.taskboard.dto.LogoutRequest
import com.taskboard.dto.RefreshRequest
import com.taskboard.dto.RegisterRequest
import com.taskboard.dto.UserResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ApiIntegrationTest {
    @Test
    fun `health reports database UP`() = withApp { client ->
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<HealthResponse>()
        assertEquals("UP", body.status)
        assertEquals("UP", body.database)
    }

    @Test
    fun `auth register login me refresh logout`() = withApp { client ->
        val email = "user-${System.currentTimeMillis()}@example.com"

        val register = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = email,
                    password = "password123",
                    displayName = "Test User",
                ),
            )
        }
        assertEquals(HttpStatusCode.Created, register.status)
        val registered = register.body<AuthResponse>()
        assertEquals(email, registered.user.email)
        assertTrue(registered.accessToken.isNotBlank())
        assertTrue(registered.refreshToken.isNotBlank())

        val me = client.get("/users/me") {
            header(HttpHeaders.Authorization, "Bearer ${registered.accessToken}")
        }
        assertEquals(HttpStatusCode.OK, me.status)
        assertEquals(email, me.body<UserResponse>().email)

        val unauthorized = client.get("/users/me")
        assertEquals(HttpStatusCode.Unauthorized, unauthorized.status)

        val login = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = email, password = "password123"))
        }
        assertEquals(HttpStatusCode.OK, login.status)
        val loggedIn = login.body<AuthResponse>()

        val refreshed = client.post("/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshRequest(refreshToken = loggedIn.refreshToken))
        }
        assertEquals(HttpStatusCode.OK, refreshed.status)
        val newTokens = refreshed.body<AuthResponse>()
        assertNotEquals(loggedIn.refreshToken, newTokens.refreshToken)

        val logout = client.post("/auth/logout") {
            contentType(ContentType.Application.Json)
            setBody(LogoutRequest(refreshToken = newTokens.refreshToken))
        }
        assertEquals(HttpStatusCode.NoContent, logout.status)

        val reuseRefresh = client.post("/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshRequest(refreshToken = newTokens.refreshToken))
        }
        assertEquals(HttpStatusCode.Unauthorized, reuseRefresh.status)
    }

    private fun withApp(block: suspend (io.ktor.client.HttpClient) -> Unit) =
        testApplication {
            environment {
                config = MapApplicationConfig(
                    "db.url" to (System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/taskboard"),
                    "db.user" to (System.getenv("DATABASE_USER") ?: "taskboard"),
                    "db.password" to (System.getenv("DATABASE_PASSWORD") ?: "taskboard"),
                    "db.maxPoolSize" to "5",
                    "jwt.secret" to "test-secret-which-is-long-enough-123456",
                    "jwt.issuer" to "taskboard-api",
                    "jwt.audience" to "taskboard-api",
                    "jwt.realm" to "TaskBoard",
                    "jwt.accessTokenTtlMinutes" to "15",
                    "jwt.refreshTokenTtlDays" to "30",
                )
            }
            application { module() }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }
            block(client)
        }
}
