package com.taskboard.di

import com.taskboard.config.jwtConfig
import com.taskboard.repository.RefreshTokenRepository
import com.taskboard.repository.UserRepository
import com.taskboard.security.BcryptPasswordHasher
import com.taskboard.security.JwtTokenService
import com.taskboard.service.AuthService
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.util.AttributeKey

/**
 * DI-контейнер
 */
data class AppServices(
    val authService: AuthService,
    val jwtTokenService: JwtTokenService,
)

val AppServicesKey = AttributeKey<AppServices>("AppServices")

val Application.services: AppServices
    get() = attributes[AppServicesKey]

val ApplicationCall.services: AppServices
    get() = application.services

fun Application.configureDependencies() {
    val jwtConfig = jwtConfig()
    val jwtTokenService = JwtTokenService(jwtConfig)
    val authService = AuthService(
        userRepository = UserRepository(),
        refreshTokenRepository = RefreshTokenRepository(),
        passwordHasher = BcryptPasswordHasher(),
        jwtTokenService = jwtTokenService,
        jwtConfig = jwtConfig,
    )

    attributes.put(
        AppServicesKey,
        AppServices(
            authService = authService,
            jwtTokenService = jwtTokenService,
        ),
    )
}
