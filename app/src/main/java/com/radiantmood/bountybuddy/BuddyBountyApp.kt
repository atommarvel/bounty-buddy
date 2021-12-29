package com.radiantmood.bountybuddy

import android.app.Application
import com.radiantmood.bountybuddy.auth.AuthManager
import com.radiantmood.bountybuddy.db.WorldContentRepository
import com.radiantmood.bountybuddy.dev.Devtool

lateinit var App: BuddyBountyApp
    private set

class BuddyBountyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        App = this
        authManager
    }

    val devtool by lazy { Devtool() }
    val authManager by lazy { AuthManager() }
    val contentRepo by lazy { WorldContentRepository() }
}