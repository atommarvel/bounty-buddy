package com.radiantmood.bountybuddy

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AuthManager {

    val clientId get() = App.getString(R.string.oauth_client_id)

    private var _authState: AuthState? = null
    var authState: AuthState
        private set(newValue) {
            writeAuthState(newValue)
            _authState = newValue
        }
        get() {
            if (_authState == null) {
                val jsonState = App.getSharedPreferences("auth", MODE_PRIVATE).getString("authState", null)
                _authState = if (jsonState != null) {
                    Json.decodeFromString<AuthState>(jsonState)
                } else AuthState()
            }
            return _authState!!
        }

    @Serializable
    data class AuthState(
        val code: String? = null,
        val token: String? = null,
        val tokenExpiry: Int? = null,
    )

    fun requestAuthorization(activity: Activity) {
        val uri = Uri.parse("https://www.bungie.net/en/oauth/authorize")
            .buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("state", "222")
            .build()
        CustomTabsIntent.Builder().build().launchUrl(activity, uri)
    }

    private val form: MediaType = "application/x-www-form-urlencoded".toMediaType()

    suspend fun requestToken() {
        val req = Request.Builder()
            .url("https://www.bungie.net/Platform/App/OAuth/token")
            .post("grant_type=authorization_code&code=${authState.code.orEmpty()}&client_id=${clientId}".toRequestBody(form))
            .build()
        withContext(Dispatchers.IO) {
            val res = RetrofitBuilder.client.newCall(req).execute()
            val parsedResponse = Json.decodeFromString<TokenResponse>(res.body?.string().orEmpty())
            authState = authState.copy(token = parsedResponse.access_token, tokenExpiry = parsedResponse.expires_in)
            Log.d("araiff", parsedResponse.toString())
        }
    }

    @Serializable
    data class TokenResponse(
        val access_token: String,
        val expires_in: Int,
        val membership_id: String,
        val token_type: String,
    )

    fun parsePossibleAuthRedirect(intent: Intent) {
        //com.radiantmood.bountybuddy://v1/oauth?code=XXX&state=XXX
        intent.data?.takeIf {
            it.scheme == "com.radiantmood.bountybuddy" &&
                it.host == "v1" &&
                it.path == "/oauth" &&
                it.getQueryParameter("state") == "222" // TODO: generate state key value
        }?.let {
            val authorizationCode = it.getQueryParameter("code")
            authState = authState.copy(code = authorizationCode)
        }
    }

    private fun writeAuthState(state: AuthState) {
        val authPrefs: SharedPreferences = App.getSharedPreferences("auth", MODE_PRIVATE)
        authPrefs.edit()
            .putString("authState", Json.encodeToString(state))
            .apply()
    }

    companion object {
        const val OAUTH_REQ_CODE = 222
    }
}