package com.taskboard.routes

import com.taskboard.dto.HealthResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.healthRoutes() {
    get("/health") {
        val databaseStatus = runCatching {
            transaction {
                exec("SELECT 1") { rs ->
                    rs.next()
                    rs.getInt(1)
                }
            }
            "UP"
        }.getOrElse { "DOWN" }

        val overall = if (databaseStatus == "UP") "UP" else "DEGRADED"
        val statusCode = if (databaseStatus == "UP") HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable

        call.respond(
            statusCode,
            HealthResponse(
                status = overall,
                service = "taskboard-api",
                version = "0.1.0",
                database = databaseStatus,
            ),
        )
    }
}
