package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
abstract class BungieResponse {
    var ErrorCode: Int? = null
    var ThrottleSeconds: Int? = null
    var ErrorStatus: String? = null
    var Message: String? = null
    var MessageData: Map<String, String>? = null
    var DetailedErrorTrace: String? = null
}