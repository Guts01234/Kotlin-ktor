package com.taskboard.domain

import java.time.OffsetDateTime
import java.util.UUID

/**
 * Domain model Entity — чистая бизнес-сущность без HTTP/SQL деталей.
 */
data class User(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val displayName: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)

data class RefreshToken(
    val id: UUID,
    val userId: UUID,
    val tokenHash: String,
    val expiresAt: OffsetDateTime,
    val createdAt: OffsetDateTime,
    val revokedAt: OffsetDateTime?,
) {
    val isActive: Boolean
        get() = revokedAt == null && expiresAt.isAfter(OffsetDateTime.now())
}
