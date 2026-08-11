package com.taskboard.repository

import com.taskboard.db.tables.RefreshTokensTable
import com.taskboard.domain.RefreshToken
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.OffsetDateTime
import java.util.UUID

class RefreshTokenRepository {
    fun create(userId: UUID, tokenHash: String, expiresAt: OffsetDateTime): RefreshToken = transaction {
        val now = OffsetDateTime.now()
        val id = UUID.randomUUID()

        RefreshTokensTable.insert {
            it[RefreshTokensTable.id] = id
            it[RefreshTokensTable.userId] = userId
            it[RefreshTokensTable.tokenHash] = tokenHash
            it[RefreshTokensTable.expiresAt] = expiresAt
            it[createdAt] = now
            it[revokedAt] = null
        }

        RefreshToken(
            id = id,
            userId = userId,
            tokenHash = tokenHash,
            expiresAt = expiresAt,
            createdAt = now,
            revokedAt = null,
        )
    }

    fun findActiveByHash(tokenHash: String): RefreshToken? = transaction {
        RefreshTokensTable
            .selectAll()
            .where { RefreshTokensTable.tokenHash eq tokenHash }
            .singleOrNull()
            ?.toRefreshToken()
            ?.takeIf { it.isActive }
    }

    fun revokeByHash(tokenHash: String): Boolean = transaction {
        val now = OffsetDateTime.now()
        RefreshTokensTable.update({ RefreshTokensTable.tokenHash eq tokenHash }) {
            it[revokedAt] = now
        } > 0
    }

    fun revokeAllForUser(userId: UUID): Int = transaction {
        val now = OffsetDateTime.now()
        RefreshTokensTable.update({
            (RefreshTokensTable.userId eq userId) and RefreshTokensTable.revokedAt.isNull()
        }) {
            it[revokedAt] = now
        }
    }

    private fun ResultRow.toRefreshToken() = RefreshToken(
        id = this[RefreshTokensTable.id].value,
        userId = this[RefreshTokensTable.userId].value,
        tokenHash = this[RefreshTokensTable.tokenHash],
        expiresAt = this[RefreshTokensTable.expiresAt],
        createdAt = this[RefreshTokensTable.createdAt],
        revokedAt = this[RefreshTokensTable.revokedAt],
    )
}
