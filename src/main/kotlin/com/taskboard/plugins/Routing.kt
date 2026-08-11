package com.taskboard.plugins

import com.taskboard.routes.authRoutes
import com.taskboard.routes.healthRoutes
import com.taskboard.routes.userRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        healthRoutes()
        authRoutes()
        userRoutes()
    }
}
