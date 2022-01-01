package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDataResponse(val Response: ProfileData) : BungieResponse()

@Serializable
data class ProfileData(
    val characters: DestinyCharacterComponentMap,
    val characterInventories: CharacterInventoryComponentMap,
    val itemComponents: ItemComponents,
)

@Serializable
data class ItemComponents(
    val objectives: ObjectivesMap
)

@Serializable
data class ObjectivesMap(
    val data: Map<Long, DestinyItemObjectivesComponent>
)

@Serializable
data class DestinyItemObjectivesComponent(
    val objectives: List<DestinyObjectiveProgress>,
    val flavorObjective: DestinyObjectiveProgress? = null,
    val dateCompleted: String? = null, // TODO: date-time
)

@Serializable
data class DestinyCharacterComponentMap(val data: Map<String, DestinyCharacterComponent>)

fun DestinyCharacterComponentMap.getPopularCharacter(): DestinyCharacterComponent? {
    var strongestCharacter: DestinyCharacterComponent? = null
    data.forEach { entry ->
        if (entry.value.light > (strongestCharacter?.light ?: 0)) {
            strongestCharacter = entry.value
        }
    }
    return strongestCharacter
}

/**
 * https://bungie-net.github.io/multi/schema_Destiny-Entities-Characters-DestinyCharacterComponent.html#schema_Destiny-Entities-Characters-DestinyCharacterComponent
 */
@Serializable
data class DestinyCharacterComponent(
    val membershipId: Long,
    val membershipType: Int,
    val characterId: Long,
    val dateLastPlayed: String, // TODO: date-time
    val minutesPlayedThisSession: Long,
    val minutesPlayedTotal: Long,
    val light: Int,
    val stats: Map<UInt, Int>,
    val raceHash: UInt,
    val genderHash: UInt,
    val classHash: UInt,
    val raceType: Int,
    val classType: Int,
    val genderType: Int,
    val emblemPath: String,
    val emblemBackgroundPath: String,
    val emblemHash: UInt,
    val emblemColor: DestinyColor,
    val titleRecordHash: UInt? = null,
)

@Serializable
data class DestinyColor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int,
)

@Serializable
data class CharacterInventoryComponentMap(
    val data: Map<Long, DestinyInventoryComponent>
)

@Serializable
data class DestinyInventoryComponent(
    val items: List<DestinyItemComponent>
)

@Serializable
data class DestinyItemComponent(
    val itemHash: UInt,
    val itemInstanceId: Long? = null,
    val quantity: Int,
    val bindStatus: Int,
    val location: Int,
    val bucketHash: UInt,
    val transferStatus: Int,
    val lockable: Boolean,
    val state: Int,
    val overrideStyleItemHash: UInt? = null,
    val expirationDate: String? = null, // TODO: date-time
    val isWrapper: Boolean,
    val metricObjective: DestinyObjectiveProgress? = null,
    val versionNumber: Int? = null,
    val itemValueVisibility: List<Boolean>? = null,
)

@Serializable
data class DestinyObjectiveProgress(
    val objectiveHash: UInt,
    val destinationHash: UInt? = null,
    val activityHash: UInt? = null,
    val progress: Int? = null,
    val completionValue: Int,
    val complete: Boolean,
    val visible: Boolean,
)

