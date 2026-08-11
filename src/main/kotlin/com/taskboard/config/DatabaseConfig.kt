package com.taskboard.config

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int,
) {
    companion object {
        fun from(config: ApplicationConfig): DatabaseConfig {
            val db = config.config("db")
            return DatabaseConfig(
                url = db.property("url").getString(),
                user = db.property("user").getString(),
                password = db.property("password").getString(),
                maxPoolSize = db.propertyOrNull("maxPoolSize")?.getString()?.toIntOrNull() ?: 10,
            )
        }
    }
}

fun Application.databaseConfig(): DatabaseConfig = DatabaseConfig.from(environment.config)
