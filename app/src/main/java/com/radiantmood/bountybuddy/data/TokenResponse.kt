package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val access_token: String,
    val expires_in: Int,
    val membership_id: String,
    val token_type: String,
)