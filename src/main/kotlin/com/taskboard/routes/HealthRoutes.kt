package com.taskboard.routes

import com.taskboard.dto.HealthResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes() {
    get("/health") {
        call.respond(
            HttpStatusCode.OK,
            HealthResponse(
                status = "UP",
                service = "taskboard-api",
                version = "0.1.0",
            ),
        )
    }
}
