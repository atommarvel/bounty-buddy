package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
class MembershipDataResponse(val Response: MembershipData) : BungieResponse()

@Serializable
data class MembershipData(val bungieNetUser: BungieNetUser, val destinyMemberships: List<DestinyUser>)

@Serializable
data class BungieNetUser(val displayName: String, val membershipId: String)

@Serializable
data class DestinyUser(val displayName: String, val membershipId: String, val applicableMembershipTypes: List<Int>)