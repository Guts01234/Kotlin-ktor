package com.taskboard.service

/**
 * Domain/application exceptions → StatusPages → единый JSON error.
 */
open class AppException(
    message: String,
    val code: String,
) : RuntimeException(message)

class ValidationException(message: String) : AppException(message, "VALIDATION_ERROR")

class ConflictException(message: String) : AppException(message, "CONFLICT")

class UnauthorizedException(message: String = "Unauthorized") : AppException(message, "UNAUTHORIZED")
