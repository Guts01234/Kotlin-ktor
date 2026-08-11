package com.taskboard.dto

import com.taskboard.domain.User
import com.taskboard.service.AuthTokens

fun User.toResponse() = UserResponse(
    id = id.toString(),
    email = email,
    displayName = displayName,
    createdAt = createdAt.toString(),
)

fun AuthTokens.toResponse() = AuthResponse(
    accessToken = accessToken,
    refreshToken = refreshToken,
    tokenType = tokenType,
    expiresIn = expiresIn,
    user = user.toResponse(),
)
