package com.radiantmood.bountybuddy.network

import com.radiantmood.bountybuddy.data.ManifestDataResponse
import com.radiantmood.bountybuddy.data.MembershipDataResponse
import com.radiantmood.bountybuddy.data.ProfileDataResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BungieService {

    @GET("Platform/User/GetMembershipsForCurrentUser/")
    suspend fun getMembership(): MembershipDataResponse

    @GET("Platform/Destiny2/Manifest/")
    suspend fun getManifest(): ManifestDataResponse

    @GET("Platform/Destiny2/{membershipType}/Profile/{destinyMembershipId}/")
    suspend fun getDestinyProfile(
        @Path("membershipType") membershipType: Int,
        @Path("destinyMembershipId") destinyMembershipId: Long,
        @Query("components") components: String,
    ): ProfileDataResponse
}