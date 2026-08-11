package com.taskboard.plugins

import com.taskboard.dto.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    code = "BAD_REQUEST",
                    message = cause.message ?: "Invalid request",
                ),
            )
        }

        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled error", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    code = "INTERNAL_ERROR",
                    message = "Unexpected server error",
                ),
            )
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ErrorResponse(
                    code = "NOT_FOUND",
                    message = "Resource not found",
                ),
            )
        }
    }
}
