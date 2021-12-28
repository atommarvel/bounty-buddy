package com.radiantmood.bountybuddy.network

import com.radiantmood.bountybuddy.data.MembershipDataResponse
import retrofit2.http.GET

interface BungieService {

    @GET("User/GetMembershipsForCurrentUser")
    suspend fun getProfile(): MembershipDataResponse
}