package com.taskboard.routes

import com.taskboard.di.services
import com.taskboard.dto.LoginRequest
import com.taskboard.dto.LogoutRequest
import com.taskboard.dto.RefreshRequest
import com.taskboard.dto.RegisterRequest
import com.taskboard.dto.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() {
    route("/auth") {
        post("/register") {
            val body = call.receive<RegisterRequest>()
            val tokens = call.services.authService.register(
                email = body.email,
                password = body.password,
                displayName = body.displayName,
            )
            call.respond(HttpStatusCode.Created, tokens.toResponse())
        }

        post("/login") {
            val body = call.receive<LoginRequest>()
            val tokens = call.services.authService.login(
                email = body.email,
                password = body.password,
            )
            call.respond(HttpStatusCode.OK, tokens.toResponse())
        }

        post("/refresh") {
            val body = call.receive<RefreshRequest>()
            val tokens = call.services.authService.refresh(body.refreshToken)
            call.respond(HttpStatusCode.OK, tokens.toResponse())
        }

        post("/logout") {
            val body = call.receive<LogoutRequest>()
            call.services.authService.logout(body.refreshToken)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
