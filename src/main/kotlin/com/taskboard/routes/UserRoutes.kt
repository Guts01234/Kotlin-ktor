package com.taskboard.routes

import com.taskboard.di.services
import com.taskboard.dto.toResponse
import com.taskboard.plugins.AUTH_JWT
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.userRoutes() {
    authenticate(AUTH_JWT) {
        route("/users") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val userId = UUID.fromString(principal.payload.subject)
                val user = call.services.authService.getUser(userId)
                call.respond(HttpStatusCode.OK, user.toResponse())
            }
        }
    }
}
