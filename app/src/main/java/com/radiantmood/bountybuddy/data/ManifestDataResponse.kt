package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
data class ManifestDataResponse(val Response: ManifestData) : BungieResponse()

@Serializable
data class ManifestData(
    val mobileWorldContentPaths: MobileWorldContentPaths,
    val version: String,
)

@Serializable
data class MobileWorldContentPaths(val en: String)

