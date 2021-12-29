package com.radiantmood.bountybuddy

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.radiantmood.bountybuddy.data.MembershipDataResponse
import com.radiantmood.bountybuddy.network.RetrofitBuilder
import com.radiantmood.bountybuddy.util.toPrettyString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

class OperationalDataFetcher(
    private val activity: Activity,
    private val statusCallback: ((String) -> Unit)
) {
    private val authManager get() = App.authManager
    private val contentRepo get() = App.contentRepo
    private val manifestUpdater = ManifestUpdater(statusCallback, activity)
    private var authState by authManager::authState
    private var membershipData: MembershipDataResponse? = null
    private var isManifestUpdated: Boolean = false
    private var itemManifestData: JsonElement? = null
    private var profileData: JsonElement? = null

    private var mutex = Mutex()

    suspend fun fetch() {
        if (!mutex.isLocked) {
            mutex.withLock {
                runState()
            }
        }
    }

    suspend fun onNewIntent(intent: Intent) {
        authManager.parsePossibleAuthRedirect(intent)
        if (authState.code != null) {
            statusCallback.invoke("Code obtained.")
            fetchToken()
        }
    }

    private suspend fun runState() {
        withContext(Dispatchers.IO) {
            when {
                (authState.code == null || (authState.isTokenExpired())) -> {
                    withContext(Dispatchers.Main) {
                        authManager.requestAuthorization(activity)
                    }
                }
                authState.isTokenExpired() -> fetchToken()
                !isManifestUpdated -> fetchManifest()
//                membershipData == null -> fetchMembership()
//                profileData == null -> fetchProfile()
                else -> accessDb()
            }
        }
    }

    private suspend fun fetchToken() {
        tryLog("Failed to get new token") {
            authManager.requestToken()
            statusCallback.invoke("Token obtained.")
            runState()
        }
    }

    private suspend fun fetchManifest() {
        tryLog("Failed to get manifest data") {
            manifestUpdater.updateManifest()
            isManifestUpdated = true
            runState()
        }
    }

    // TODO: only download if the current db is out of date


    private suspend fun fetchMembership() {
        tryLog("Failed to get membership data") {
            membershipData = RetrofitBuilder.bungieService.getMembership()
            statusCallback.invoke("Membership data obtained.")
            runState()
        }
    }

    private suspend fun fetchProfile() {
        tryLog("Failed to get profile") {
            profileData = RetrofitBuilder.bungieService.getDestinyProfile(
                membershipType = 3, // TODO: don't hard-code membership type and figure out how to map the right id to the right type. profileData!!.Response.destinyMemberships.first().applicableMembershipTypes.first(),
                destinyMembershipId = membershipData!!.Response.destinyMemberships.first().membershipId,
                components = "201",//"100,101,102,103,104,105,200,201,202,203,204,205,300,301,302,303,304,305,306,307,208,309,310,400,401,402,500,600,700,800,900,1000,1100,1200",
            )
            statusCallback.invoke("Profile obtained.")
            runState()
        }
    }

    private suspend fun accessDb() {
        tryLog("failed to access db") {
            val item = contentRepo.getItem("2147433548")
            item?.let {
                statusCallback.invoke(it.toPrettyString())
            }
        }
    }

    private inline fun tryLog(failMsg: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            Log.e("araiff", e.message, e)
            statusCallback.invoke(failMsg)
        }
    }
}