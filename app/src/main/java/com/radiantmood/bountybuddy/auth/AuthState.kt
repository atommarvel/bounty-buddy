package com.radiantmood.bountybuddy.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthState(
    val code: String? = null,
    val token: String? = null,
    val tokenExpiry: Int? = null,
)