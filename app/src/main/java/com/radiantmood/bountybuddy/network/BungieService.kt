package com.radiantmood.bountybuddy.network

import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET

interface BungieService {

    @GET("User/GetMembershipsForCurrentUser")
    suspend fun getProfile(): JsonElement
}