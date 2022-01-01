package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val access_token: String? = null,
    val expires_in: Int? = null,
    val membership_id: Long? = null,
    val token_type: String? = null,
    val error: String? = null,
    val error_description: String? = null,
)