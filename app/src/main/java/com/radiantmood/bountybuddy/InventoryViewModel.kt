package com.radiantmood.bountybuddy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radiantmood.bountybuddy.data.DestinyItemComponent
import com.radiantmood.bountybuddy.data.InventoryItemDefinition
import com.radiantmood.bountybuddy.data.getPopularCharacter
import com.radiantmood.bountybuddy.util.toPrettyString
import com.radiantmood.bountybuddy.util.tryLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryViewModel : ViewModel() {
    private val contentRepo get() = App.contentRepo
    private val _bounties = MutableLiveData<List<InventoryItemDefinition>>(listOf())
    val bounties: LiveData<List<InventoryItemDefinition>> get() = _bounties
    private val profileRepository get() = App.profileRepo

    fun fetchBounties() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!App.authManager.isLoggedOut()) {
                profileRepository.fetchProfile()
                compileInventoryData()
            }
        }
    }

    private suspend fun compileInventoryData() {
        tryLog {
            val profileData = profileRepository.profileData
            val strongestChar = checkNotNull(profileData?.Response?.characters?.getPopularCharacter())
            val inventory = checkNotNull(profileData?.Response?.characterInventories?.data?.get(strongestChar.characterId)?.items)
            val inventoryBounties = mutableListOf<DestinyItemComponent>()
            val bountyContent = inventory.mapNotNull { inventoryItem ->
                contentRepo.getItem(inventoryItem.itemHash)?.let { item ->
                    if (item.itemType == 26) {
                        inventoryBounties.add(inventoryItem)
                        item
                    } else null
                }
            }
            val bountyNames = bountyContent.map { it.displayProperties.name }
            _bounties.postValue(bountyContent)
            Log.v("araiff", bountyNames.toString())
            Log.v("araiff", inventoryBounties.first().toPrettyString())
            Log.v("araiff", "\n\nWORLD CONTENT DATAn\n\n")
            Log.v("araiff", bountyContent.first().toPrettyString())
        }
    }
}