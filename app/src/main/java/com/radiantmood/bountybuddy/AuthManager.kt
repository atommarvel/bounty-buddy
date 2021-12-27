package com.radiantmood.bountybuddy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class AuthManager {

    private val config by lazy {
        AuthorizationServiceConfiguration(
            Uri.parse("https://www.bungie.net/en/OAuth/Authorize"), // authorization endpoint
            Uri.parse("https://www.bungie.net/Platform/App/OAuth/token") // token endpoint
        )
    }

    private val request by lazy {
        AuthorizationRequest.Builder(
            config,  // the authorization service configuration
            App.getString(R.string.oauth_client_id),  // the client ID, typically pre-registered and static
            ResponseTypeValues.CODE,  // the response_type value: we want a code
            Uri.parse("com.radiantmood.bountybuddy://v1/oauth") // the redirect URI to which the auth response is sent
        ).build()
    }

    val authState by lazy { AuthState(config) }

    fun login(activity: Activity) {
        val authService = AuthorizationService(App)
        val authIntent = authService.getAuthorizationRequestIntent(request)
        activity.startActivityForResult(authIntent, 222)
    }

    fun updateAuthState(intent: Intent) {
        val res = AuthorizationResponse.fromIntent(intent)
        val e = AuthorizationException.fromIntent(intent)
        if (res != null || e != null) {
            authState.update(res, e)
        }
    }
}