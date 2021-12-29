package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

/**
 * Base class for most responses in Bungie's API
 */
@Serializable
abstract class BungieResponse {
    var ErrorCode: Int? = null
    var ThrottleSeconds: Int? = null
    var ErrorStatus: String? = null
    var Message: String? = null
    var MessageData: Map<String, String>? = null
    var DetailedErrorTrace: String? = null
}