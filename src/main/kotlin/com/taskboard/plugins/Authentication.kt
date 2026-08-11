package com.taskboard.plugins

import com.taskboard.config.jwtConfig
import com.taskboard.di.services
import com.taskboard.dto.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

const val AUTH_JWT = "auth-jwt"

fun Application.configureAuthentication() {
    val jwt = jwtConfig()
    val jwtTokenService = services.jwtTokenService

    install(Authentication) {
        jwt(AUTH_JWT) {
            realm = jwt.realm
            verifier(jwtTokenService.verifier)
            validate { credential ->
                val userId = credential.payload.subject
                if (userId.isNullOrBlank()) {
                    null
                } else {
                    JWTPrincipal(credential.payload)
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(
                        code = "UNAUTHORIZED",
                        message = "Valid JWT required",
                    ),
                )
            }
        }
    }
}
