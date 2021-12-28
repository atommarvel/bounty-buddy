package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
class MembershipDataResponse(val Response: MembershipData) : BungieResponse()

@Serializable
data class MembershipData(val bungieNetUser: BungieNetUser)

@Serializable
data class BungieNetUser(val displayName: String)