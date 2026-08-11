package com.taskboard.config

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val accessTokenTtlMinutes: Long,
    val refreshTokenTtlDays: Long,
) {
    companion object {
        fun from(config: ApplicationConfig): JwtConfig {
            val jwt = config.config("jwt")
            return JwtConfig(
                secret = jwt.property("secret").getString(),
                issuer = jwt.property("issuer").getString(),
                audience = jwt.property("audience").getString(),
                realm = jwt.property("realm").getString(),
                accessTokenTtlMinutes = jwt.property("accessTokenTtlMinutes").getString().toLong(),
                refreshTokenTtlDays = jwt.property("refreshTokenTtlDays").getString().toLong(),
            )
        }
    }
}

fun Application.jwtConfig(): JwtConfig = JwtConfig.from(environment.config)
