package com.taskboard.plugins

import com.taskboard.dto.ErrorResponse
import com.taskboard.service.AppException
import com.taskboard.service.ConflictException
import com.taskboard.service.UnauthorizedException
import com.taskboard.service.ValidationException
import io.ktor.http.HttpStatusCode
import io.ktor.http.parsing.ParseException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(code = cause.code, message = cause.message ?: "Validation error"),
            )
        }

        exception<ConflictException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(code = cause.code, message = cause.message ?: "Conflict"),
            )
        }

        exception<UnauthorizedException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(code = cause.code, message = cause.message ?: "Unauthorized"),
            )
        }

        // Malformed Authorization header (e.g. placeholder <accessToken>) → 401, not 500.
        exception<ParseException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    code = "UNAUTHORIZED",
                    message = cause.message ?: "Invalid Authorization header",
                ),
            )
        }

        exception<AppException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(code = cause.code, message = cause.message ?: "Application error"),
            )
        }

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
