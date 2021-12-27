package com.radiantmood.bountybuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.radiantmood.bountybuddy.ui.theme.BountyBuddyTheme

class MainActivity : ComponentActivity() {

    private val authManager by lazy { AuthManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager.updateAuthState(intent)
        comp()
    }

    private fun comp() {
        setContent {
            Screen(status = authManager.authState.jsonSerializeString()) {
                authManager.login(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        comp()
    }

    @Suppress("Deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 222 && data != null) {
            authManager.updateAuthState(data)
        }
    }
}

@Composable
fun Screen(status: String, onLoginClick: () -> Unit) {
    BountyBuddyTheme {
        Surface(color = MaterialTheme.colors.background) {
            LazyColumn {
                item { Button(onClick = onLoginClick) { Text("Login") } }
                item { Text(status) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Screen("Hello World! ".repeat(200)) {}
}