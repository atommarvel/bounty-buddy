package com.radiantmood.bountybuddy

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface BungieService {

    @GET("User/GetMembershipsForCurrentUser")
    suspend fun getProfile(): ProfileResponse
}

@Serializable
data class ProfileResponse(
    val Response: String?
)