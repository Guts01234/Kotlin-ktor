package com.taskboard.security

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Хэширование паролей.
 */
interface PasswordHasher {
    fun hash(rawPassword: String): String
    fun verify(rawPassword: String, passwordHash: String): Boolean
}

class BcryptPasswordHasher(
    private val cost: Int = 12,
) : PasswordHasher {
    override fun hash(rawPassword: String): String =
        BCrypt.withDefaults().hashToString(cost, rawPassword.toCharArray())

    override fun verify(rawPassword: String, passwordHash: String): Boolean =
        BCrypt.verifyer().verify(rawPassword.toCharArray(), passwordHash).verified
}
