package com.radiantmood.bountybuddy.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.radiantmood.bountybuddy.App
import com.radiantmood.bountybuddy.R
import com.radiantmood.bountybuddy.data.TokenResponse
import com.radiantmood.bountybuddy.network.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar

fun calendarIn(futureSeconds: Int): Calendar =
    Calendar.getInstance().apply {
        add(Calendar.SECOND, futureSeconds)
    }

class AuthManager {

    private val clientId get() = App.getString(R.string.oauth_client_id)

    var authState: AuthState by AuthStateDelegate()

    fun requestAuthorization(activity: Activity) {
        val uri = Uri.parse("https://www.bungie.net/en/oauth/authorize/")
            .buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("state", OAUTH_REQ_CODE)
            .build()
        CustomTabsIntent.Builder().build().launchUrl(activity, uri)
    }

    suspend fun requestToken() {
        val form = "application/x-www-form-urlencoded".toMediaType()
        val req = Request.Builder()
            .url("https://www.bungie.net/Platform/App/OAuth/token/")
            .post("grant_type=authorization_code&code=${authState.code.orEmpty()}&client_id=${clientId}".toRequestBody(form))
            .build()
        withContext(Dispatchers.IO) {
            val res = RetrofitBuilder.authClient.newCall(req).execute()
            val parsedResponse = Json.decodeFromString<TokenResponse>(res.body?.string().orEmpty())
            authState = authState.copy(token = parsedResponse.access_token, tokenExpiry = calendarIn(parsedResponse.expires_in))
            Log.d("araiff", parsedResponse.toString())
        }
    }

    fun parsePossibleAuthRedirect(intent: Intent) {
        //com.radiantmood.bountybuddy://v1/oauth?code=XXX&state=XXX
        intent.data?.takeIf { it.isAuthRedirect() }?.let {
            val authorizationCode = it.getQueryParameter("code")
            authState = authState.copy(code = authorizationCode)
        }
    }

    private fun Uri.isAuthRedirect() = (scheme == "com.radiantmood.bountybuddy" &&
        host == "v1" &&
        path == "/oauth" &&
        getQueryParameter("state") == OAUTH_REQ_CODE)

    companion object {
        const val OAUTH_REQ_CODE = "222" // TODO: generate state key value
    }
}