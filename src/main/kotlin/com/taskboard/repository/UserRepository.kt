package com.taskboard.repository

import com.taskboard.db.tables.UsersTable
import com.taskboard.domain.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime
import java.util.UUID

class UserRepository {
    fun findById(id: UUID): User? = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    fun findByEmail(email: String): User? = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.email eq email.lowercase() }
            .singleOrNull()
            ?.toUser()
    }

    fun create(email: String, passwordHash: String, displayName: String): User = transaction {
        val now = OffsetDateTime.now()
        val id = UUID.randomUUID()

        UsersTable.insert {
            it[UsersTable.id] = id
            it[UsersTable.email] = email.lowercase()
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.displayName] = displayName
            it[createdAt] = now
            it[updatedAt] = now
        }

        User(
            id = id,
            email = email.lowercase(),
            passwordHash = passwordHash,
            displayName = displayName,
            createdAt = now,
            updatedAt = now,
        )
    }

    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id].value,
        email = this[UsersTable.email],
        passwordHash = this[UsersTable.passwordHash],
        displayName = this[UsersTable.displayName],
        createdAt = this[UsersTable.createdAt],
        updatedAt = this[UsersTable.updatedAt],
    )
}
