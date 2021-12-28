package com.radiantmood.bountybuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.radiantmood.bountybuddy.network.RetrofitBuilder
import com.radiantmood.bountybuddy.ui.theme.BountyBuddyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val authManager get() = App.authManager
    private var userInfoData: String? = null
    private var manifestData: String? = null
    private var bountyData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comp()
    }

    override fun onNewIntent(intent: Intent) {
        authManager.parsePossibleAuthRedirect(intent)
        comp()
    }

    private fun onAuthorizeClick() {
        authManager.requestAuthorization(this)
    }

    private fun onTokenizeClick() {
        lifecycleScope.launch {
            authManager.requestToken()
            withContext(Dispatchers.Main) {
                comp()
            }
        }
    }

    private fun onUserInfoReqClick() {
        lifecycleScope.launch {
            try {
                val profile = RetrofitBuilder.bungieService.getProfile()
                Log.d("araiff", profile.toString())
                userInfoData = "DisplayName: ${profile.Response.bungieNetUser.displayName}"
                withContext(Dispatchers.Main) {
                    comp()
                }
            } catch (e: Exception) {
                Log.e("araiff", e.message.orEmpty())
            }
        }
    }

    private fun onManifestReqClick() {
        // TODO
    }

    private fun onBountyReqClick() {
        // TODO
    }

    private fun comp() {
        setContent {
            Screen(
                onAuthorizeClick = ::onAuthorizeClick,
                onTokenizeClick = ::onTokenizeClick,
                authorizedStatus = authManager.authState.toString(),
                onManifestReqClick = ::onManifestReqClick, manifestData = manifestData,
                onUserInfoReqClick = ::onUserInfoReqClick, userInfoData = userInfoData,
                onBountyReqClick = ::onBountyReqClick, bountyData = bountyData,
            )
        }
    }
}

@Composable
fun Screen(
    onAuthorizeClick: () -> Unit, onTokenizeClick: () -> Unit,
    authorizedStatus: String,
    onManifestReqClick: () -> Unit, manifestData: String?,
    onUserInfoReqClick: () -> Unit, userInfoData: String?,
    onBountyReqClick: () -> Unit, bountyData: String?,
) {
    BountyBuddyTheme {
        Surface(color = MaterialTheme.colors.background) {
            LazyColumn {
                item {
                    Row {
                        Button(onClick = onAuthorizeClick) { Text("Authorize") }
                        Button(onClick = onTokenizeClick) { Text("Get Token") }
                    }
                }
                item { Text(authorizedStatus) }
                item { Divider() }
                item { Button(onClick = onManifestReqClick) { Text("Make Manifest Request") } }
                item { Text(manifestData.orEmpty()) }
                item { Divider() }
                item { Button(onClick = onUserInfoReqClick) { Text("Make User Info Request") } }
                item { Text(userInfoData.orEmpty()) }
                item { Divider() }
                item { Button(onClick = onBountyReqClick) { Text("Make Bounty Request") } }
                item { Text(bountyData.orEmpty()) }
                item { Divider() }
            }
        }
    }
}