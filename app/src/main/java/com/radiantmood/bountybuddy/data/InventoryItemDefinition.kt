package com.radiantmood.bountybuddy.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class InventoryItemDefinition(
    val displayProperties: DestinyDisplayPropertiesDefinition,
    val collectibleHash: UInt? = null,
    val iconWatermark: String? = null,
    val iconWatermarkShelved: String? = null,
    val secondaryIcon: String? = null,
    val secondaryOverlay: String? = null,
    val secondarySpecial: String? = null,
    val backgroundColor: DestinyColor? = null,
    val screenshot: String? = null,
    val itemTypeDisplayName: String,
    val flavorText: String,
    val uiItemDisplayStyle: String,
    val itemTypeAndTierDisplayName: String,
    val displaySource: String,
    val tooltipStyle: String? = null,
    val action: JsonElement? = null, //DestinyItemActionBlockDefinition
    val inventory: DestinyItemInventoryBlockDefinition,
    val setData: DestinyItemSetBlockDefinition? = null,
    val stats: JsonElement? = null, // DestinyItemStatBlockDefinition
    val emblemObjectiveHash: UInt? = null,
    val equippingBlock: JsonElement? = null, // DestinyEquippingBlockDefinition
    val translationBlock: JsonElement? = null, // DestinyItemTranslationBlockDefinition
    val preview: JsonElement? = null, // DestinyItemPreviewBlockDefinition
    val quality: JsonElement? = null, // DestinyItemQualityBlockDefinition
    val value: JsonElement? = null, // DestinyItemValueBlockDefinition
    val sourceData: JsonElement? = null, // DestinyItemSourceBlockDefinition
    val objectives: DestinyItemObjectiveBlockDefinition? = null,
    val metrics: JsonElement? = null, // DestinyItemMetricBlockDefinition
    val plug: JsonElement? = null, // DestinyItemPlugDefinition
    val gearset: JsonElement? = null, // DestinyItemGearsetBlockDefinition
    val sack: JsonElement? = null, // DestinyItemSackBlockDefinition
    val sockets: JsonElement? = null, // DestinyItemSocketBlockDefinition
    val summary: JsonElement? = null, // DestinyItemSummaryBlockDefinition
    val talentGrid: JsonElement? = null, // DestinyItemTalentGridBlockDefinition
    val investmentStats: JsonElement? = null, // DestinyItemInvestmentStatDefinition
    val perks: JsonElement? = null, // DestinyItemPerkEntryDefinition
    val loreHash: UInt? = null,
    val summaryItemHash: UInt? = null,
    val animations: JsonElement? = null, // DestinyAnimationReference
    val allowActions: Boolean,
    val links: JsonElement? = null, // HyperlinkReference
    val doesPostmasterPullHaveSideEffects: Boolean,
    val nonTransferrable: Boolean,
    val itemCategoryHashes: List<UInt>,
    val specialItemType: Int,
    val itemType: Int,
    val itemSubType: Int,
    val classType: Int,
    val breakerType: Int,
    val breakerTypeHash: UInt? = null,
    val equippable: Boolean,
    val damageTypeHashes: List<UInt>? = null,
    val damageTypes: List<Int>? = null,
    val defaultDamageType: Int,
    val defaultDamageTypeHash: UInt? = null,
    val seasonHash: UInt? = null,
    val isWrapper: Boolean,
    val traitIds: List<String>? = null,
    val traitHashes: List<UInt>? = null,
    val hash: UInt,
    val index: Int,
    val redacted: Boolean,
)

@Serializable
data class DestinyDisplayPropertiesDefinition(
    val description: String,
    val name: String,
    val icon: String? = null,
    val iconSequences: List<DestinyIconSequenceDefinition>? = null,
    val highResIcon: String? = null,
    val hasIcon: Boolean,
)

@Serializable
data class DestinyIconSequenceDefinition(
    val frames: List<String>
)

@Serializable
data class DestinyItemInventoryBlockDefinition(
    val stackUniqueLabel: String? = null,
    val maxStackSize: Int,
    val bucketTypeHash: UInt,
    val recoveryBucketTypeHash: UInt,
    val tierTypeHash: UInt,
    val isInstanceItem: Boolean,
    val tierTypeName: String? = null,
    val tierType: Int,
    val expirationTooltip: String,
    val expiredInActivityMessage: String,
    val expiredInOrbitMessage: String,
    val suppressExpirationWhenObjectivesComplete: Boolean,
)

@Serializable
data class DestinyItemSetBlockDefinition(
    val itemList: JsonElement, // DestinyItemSetBlockEntryDefinition
    val requireOrderedSetItemAdd: Boolean,
    val setIsFeatured: Boolean,
    val setType: String,
    val questLineName: String,
    val questLineDescription: String,
    val questStepSummary: String,
)

@Serializable
data class DestinyItemObjectiveBlockDefinition(
    val objectiveHashes: List<UInt>,
    val displayActivityHashes: List<UInt>,
    val requireFullObjectiveCompletion: Boolean,
    val questlineItemHash: UInt,
    val narrative: String,
    val objectiveVerbName: String,
    val questTypeIdentifier: String,
    val questTypeHash: UInt,
    val perObjectiveDisplayProperties: List<DestinyObjectiveDisplayProperties>,
    val displayAsStatTracker: Boolean,
)

@Serializable
data class DestinyObjectiveDisplayProperties(
    val activityHash: UInt? = null,
    val displayOnItemPreviewScreen: Boolean,
)