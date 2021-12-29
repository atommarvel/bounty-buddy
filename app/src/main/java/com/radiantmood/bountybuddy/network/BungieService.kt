package com.radiantmood.bountybuddy.network

import com.radiantmood.bountybuddy.data.MembershipDataResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BungieService {

    @GET("Platform/User/GetMembershipsForCurrentUser/")
    suspend fun getMembership(): MembershipDataResponse

    @GET("Platform/Destiny2/Manifest/")
    suspend fun getManifest(): JsonElement

    @GET("Platform/Destiny2/{membershipType}/Profile/{destinyMembershipId}/")
    suspend fun getDestinyProfile(
        @Path("membershipType") membershipType: Int,
        @Path("destinyMembershipId") destinyMembershipId: String,
        @Query("components") components: String,
    ): JsonElement
}