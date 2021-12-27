package com.radiantmood.bountybuddy

import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET

interface BungieService {

    @GET("User/GetMembershipsForCurrentUser")
    suspend fun getProfile(): JsonElement
}

//@Serializable
//data class ProfileResponse(
//    val Response:
//)