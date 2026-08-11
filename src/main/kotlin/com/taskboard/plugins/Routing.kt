package com.taskboard.plugins

import com.taskboard.routes.healthRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        healthRoutes()
    }
}
