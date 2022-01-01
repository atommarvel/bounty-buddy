package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
data class MembershipDataResponse(val Response: UserMembershipData) : BungieResponse()

@Serializable
data class UserMembershipData(
    val bungieNetUser: GeneralUser,
    val destinyMemberships: List<GroupUserInfoCard>,
    val primaryMembershipId: String? = null,
)

@Serializable
data class GeneralUser(
    val membershipId: Long,
    val uniqueName: String,
    val normalizedName: String? = null,
    val displayName: String,
    val profilePicture: Int,
    val profileTheme: Int,
    val userTitle: Int,
    val successMessageFlags: Long,
    val isDeleted: Boolean,
    val about: String,
    val firstAccess: String? = null, // TODO: date-time
    val lastUpdate: String? = null, // TODO: date-time
    val legacyPortalUID: Long? = null,
    val context: UserToUserContext,
    val psnDisplayName: String? = null,
    val xboxDisplayName: String? = null,
    val fbDisplayName: String? = null,
    val showActivity: Boolean? = null,
    val locale: String,
    val localeInheritDefault: Boolean,
    val lastBanReportId: Long? = null,
    val showGroupMessaging: Boolean,
    val profilePicturePath: String,
    val profilePictureWidePath: String? = null,
    val profileThemeName: String,
    val userTitleDisplay: String,
    val statusText: String,
    val statusDate: String, // TODO: date-time
    val profileBanExpire: String? = null, // TODO: date-time
    val blizzardDisplayName: String? = null,
    val steamDisplayName: String? = null,
    val stadiaDisplayName: String? = null,
    val twitchDisplayName: String? = null,
    val cachedBungieGlobalDisplayName: String,
    val cachedBungieGlobalDisplayNameCode: String? = null,
)

@Serializable
data class UserToUserContext(
    val isFollowing: Boolean,
    val ignoreStatus: IgnoreResponse,
    val globalIgnoreEndDate: String? = null, // TODO: date-time
)

@Serializable
data class IgnoreResponse(
    val isIgnored: Boolean,
    val ignoreFlags: Int,
)

/**
 * https://bungie-net.github.io/multi/schema_GroupsV2-GroupUserInfoCard.html#schema_GroupsV2-GroupUserInfoCard
 */
@Serializable
data class GroupUserInfoCard(
    val LastSeenDisplayName: String,
    val LastSeenDisplayNameType: Int,
    val supplementalDisplayName: String? = null,
    val iconPath: String,
    val crossSaveOverride: Int,
    val applicableMembershipTypes: List<Int>,
    val isPublic: Boolean,
    val membershipType: Int,
    val membershipId: Long,
    val displayName: String,
    val bungieGlobalDisplayName: String,
    val bungieGlobalDisplayNameCode: String? = null,
)