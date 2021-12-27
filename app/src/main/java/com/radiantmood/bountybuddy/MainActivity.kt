package com.radiantmood.bountybuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.radiantmood.bountybuddy.ui.theme.BountyBuddyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val authManager get() = App.authManager
    private var responseData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comp()
    }

    override fun onNewIntent(intent: Intent) {
        authManager.parsePossibleAuthRedirect(intent)
        comp()
    }

    private fun comp() {
        setContent {
            Screen(
                authorizedStatus = authManager.authState.toString(),
                responseData = responseData,
                onAuthorizeClick = {
                    authManager.requestAuthorization(this)
                },
                onTokenizeClick = {
                    lifecycleScope.launch {
                        authManager.requestToken()
                        withContext(Dispatchers.Main) {
                            comp()
                        }
                    }
                },
                onRequestClick = {
                    lifecycleScope.launch {
                        try {
                            val profile = RetrofitBuilder.bungieService.getProfile()
                            Log.d("araiff", profile.toString())
                        } catch (e: Exception) {
                            Log.e("araiff", e.message.orEmpty())
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun Screen(
    authorizedStatus: String,
    responseData: String?,
    onAuthorizeClick: () -> Unit,
    onTokenizeClick: () -> Unit,
    onRequestClick: () -> Unit
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
                item { Button(onClick = onRequestClick) { Text("Make Request") } }
                item { Text(responseData.orEmpty()) }
            }
        }
    }
}