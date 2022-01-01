package com.radiantmood.bountybuddy

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class OperationalDataFetcher(
    private val activity: Activity
) {
    var isDataAssembled by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val authManager get() = App.authManager
    private val manifestUpdater = ManifestUpdater(activity)
    private val profileRepository get() = App.profileRepo

    private var mutex = Mutex()

    suspend fun assembleData(isUserClick: Boolean = true) {
        if (!mutex.isLocked) {
            mutex.withLock {
                runState(isUserClick)
            }
        }
    }

    suspend fun onNewIntent(intent: Intent) {
        authManager.authenticateIfNeeded(intent)
        runState(false)
    }

    private suspend fun runState(isUserClick: Boolean) {
        try {
            isLoading = true
            withContext(Dispatchers.IO) {
                when {
                    authManager.isLoggedOut() && isUserClick -> authManager.requestAuthorization(activity)
                    authManager.isLoggedOut() && !isUserClick -> { /* Do nothing. User need to tap login button.*/
                    }
                    !manifestUpdater.isManifestUpdated -> {
                        manifestUpdater.updateManifest()
                        runState(isUserClick)
                    }
                    (profileRepository.profileData == null) -> {
                        profileRepository.fetchProfile()
                        runState(isUserClick)
                    }
                    else -> isDataAssembled = true
                }
            }
        } finally {
            isLoading = false
        }
    }
}