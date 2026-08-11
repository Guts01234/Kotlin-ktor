package com.taskboard.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import com.taskboard.config.JwtConfig
import java.time.Instant
import java.util.Date
import java.util.UUID

/**
 * Создание и проверка access JWT.
 * Refresh token — отдельный opaque-токен, хранится в БД (хеш), не внутри JWT.
 */
class JwtTokenService(
    private val config: JwtConfig,
) {
    private val algorithm: Algorithm = Algorithm.HMAC256(config.secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(config.issuer)
        .withAudience(config.audience)
        .build()

    fun createAccessToken(userId: UUID, email: String): String {
        val now = Instant.now()
        val expiresAt = now.plusSeconds(config.accessTokenTtlMinutes * 60)

        return JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiresAt))
            .sign(algorithm)
    }

    fun verify(token: String): DecodedJWT = verifier.verify(token)

    fun accessTokenTtlSeconds(): Long = config.accessTokenTtlMinutes * 60
}
