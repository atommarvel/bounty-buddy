package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDataResponse(val Response: ProfileData) : BungieResponse()

@Serializable
data class ProfileData(
    val characters: DestinyCharacters,
    val characterInventories: CharacterInventories,
)

@Serializable
data class DestinyCharacters(val data: Map<String, DestinyCharacter>)

fun DestinyCharacters.getPopularCharacter(): DestinyCharacter? {
    var strongestCharacter: DestinyCharacter? = null
    data.forEach { entry ->
        if (entry.value.light > (strongestCharacter?.light ?: 0)) {
            strongestCharacter = entry.value
        }
    }
    return strongestCharacter
}

@Serializable
data class DestinyCharacter(
    val characterId: String,
    val light: Int,
    val dateLastPlayed: String, // TODO: use this to pick current character
    val classType: Int,
    val raceType: Int,
)

@Serializable
data class CharacterInventories(val data: Map<String, CharacterInventory>)

@Serializable
data class CharacterInventory(
    val items: List<InventoryItem>
)

@Serializable
data class InventoryItem(
    val itemHash: String,
    val itemInstanceId: String? = null,
    val quantity: Int,
    val bindStatus: Int,
    val location: Int,
    val bucketHash: String,
    val transferStatus: Int,
    val lockable: Boolean,
    val state: Int,
    val dismantlePermission: Int,
    val isWrapper: Boolean,
    val versionNumber: Int? = null,
)

