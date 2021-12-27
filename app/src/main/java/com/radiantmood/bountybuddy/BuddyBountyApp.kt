package com.radiantmood.bountybuddy

import android.app.Application

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
}