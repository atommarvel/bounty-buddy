package com.radiantmood.bountybuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val operationalDataFetcher by lazy { OperationalDataFetcher(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comp()
        lifecycleScope.launch {
            operationalDataFetcher.assembleData(false)
        }
    }

    override fun onNewIntent(intent: Intent) {
        lifecycleScope.launch { operationalDataFetcher.onNewIntent(intent) }
    }

    private fun comp() {
        setContent {
            DebugScreen(
                isLoggedIn = operationalDataFetcher.isDataAssembled,
                isLoading = operationalDataFetcher.isLoading
            ) {
                lifecycleScope.launch {
                    operationalDataFetcher.assembleData()
                }
            }
        }
    }
}