package com.taskboard.service

import com.taskboard.config.JwtConfig
import com.taskboard.domain.User
import com.taskboard.repository.RefreshTokenRepository
import com.taskboard.repository.UserRepository
import com.taskboard.security.JwtTokenService
import com.taskboard.security.PasswordHasher
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.util.HexFormat
import java.util.UUID

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: User,
)

/**
 * Application service = use-case слой.
 */
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtTokenService: JwtTokenService,
    private val jwtConfig: JwtConfig,
) {
    fun register(email: String, password: String, displayName: String): AuthTokens {
        val normalizedEmail = email.trim().lowercase()
        val normalizedName = displayName.trim()

        validateEmail(normalizedEmail)
        validatePassword(password)
        if (normalizedName.isEmpty()) {
            throw ValidationException("Display name must not be blank")
        }

        if (userRepository.findByEmail(normalizedEmail) != null) {
            throw ConflictException("User with this email already exists")
        }

        val user = userRepository.create(
            email = normalizedEmail,
            passwordHash = passwordHasher.hash(password),
            displayName = normalizedName,
        )
        return issueTokens(user)
    }

    fun login(email: String, password: String): AuthTokens {
        val user = userRepository.findByEmail(email.trim().lowercase())
            ?: throw UnauthorizedException("Invalid email or password")

        if (!passwordHasher.verify(password, user.passwordHash)) {
            throw UnauthorizedException("Invalid email or password")
        }

        return issueTokens(user)
    }

    fun refresh(rawRefreshToken: String): AuthTokens {
        val hash = hashToken(rawRefreshToken)
        val stored = refreshTokenRepository.findActiveByHash(hash)
            ?: throw UnauthorizedException("Invalid or expired refresh token")

        val user = userRepository.findById(stored.userId)
            ?: throw UnauthorizedException("Invalid or expired refresh token")

        refreshTokenRepository.revokeByHash(hash)
        return issueTokens(user)
    }

    fun logout(rawRefreshToken: String) {
        refreshTokenRepository.revokeByHash(hashToken(rawRefreshToken))
    }

    fun getUser(userId: UUID): User =
        userRepository.findById(userId) ?: throw UnauthorizedException("User not found")

    private fun issueTokens(user: User): AuthTokens {
        val accessToken = jwtTokenService.createAccessToken(user.id, user.email)
        val rawRefreshToken = UUID.randomUUID().toString() + UUID.randomUUID().toString()
        val expiresAt = OffsetDateTime.now().plusDays(jwtConfig.refreshTokenTtlDays)

        refreshTokenRepository.create(
            userId = user.id,
            tokenHash = hashToken(rawRefreshToken),
            expiresAt = expiresAt,
        )

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = rawRefreshToken,
            expiresIn = jwtTokenService.accessTokenTtlSeconds(),
            user = user,
        )
    }

    private fun validateEmail(email: String) {
        if (!email.contains("@") || email.length < 5) {
            throw ValidationException("Invalid email")
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < 8) {
            throw ValidationException("Password must be at least 8 characters")
        }
    }

    private fun hashToken(rawToken: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(rawToken.toByteArray())
        return HexFormat.of().formatHex(digest)
    }
}
