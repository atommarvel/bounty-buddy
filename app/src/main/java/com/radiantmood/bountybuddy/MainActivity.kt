package com.radiantmood.bountybuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val operationalDataFetcher by lazy { OperationalDataFetcher(this, ::onStatusUpdated) }
    var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comp()
    }

    override fun onNewIntent(intent: Intent) {
        lifecycleScope.launch { operationalDataFetcher.onNewIntent(intent) }
    }

    private fun onStatusUpdated(status: String) {
        this.status = this.status + status + "\n"
        comp()
    }

    private fun comp() {
        setContent {
            DebugScreen(status = status) {
                lifecycleScope.launch {
                    operationalDataFetcher.fetch()
                }
            }
        }
    }
}