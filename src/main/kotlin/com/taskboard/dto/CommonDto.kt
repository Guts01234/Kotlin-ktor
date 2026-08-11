package com.taskboard.dto

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val service: String,
    val version: String,
    val database: String? = null,
)

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
)
