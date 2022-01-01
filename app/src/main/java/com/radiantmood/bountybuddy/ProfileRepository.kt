package com.radiantmood.bountybuddy

import android.util.Log
import com.radiantmood.bountybuddy.data.MembershipDataResponse
import com.radiantmood.bountybuddy.data.ProfileDataResponse
import com.radiantmood.bountybuddy.network.RetrofitBuilder
import com.radiantmood.bountybuddy.util.tryLog

class ProfileRepository {
    private var membershipData: MembershipDataResponse? = null
    var profileData: ProfileDataResponse? = null

    suspend fun fetchProfile() {
        tryLog {
            fetchMembership()
            profileData = RetrofitBuilder.bungieService.getDestinyProfile(
                /**
                 * TODO: don't hard-code membership type and figure out how to map the right id to the right type.
                 * profileData!!.Response.destinyMemberships.first().applicableMembershipTypes.first()
                 */
                membershipType = 3,
                destinyMembershipId = membershipData!!.Response.destinyMemberships.first().membershipId,
                components = "200,201,301",
            )
            Log.v("araiff", "Profile obtained")
        }
    }

    private suspend fun fetchMembership() {
        membershipData = RetrofitBuilder.bungieService.getMembership()
        Log.v("araiff", "Membership data obtained.")
    }
}