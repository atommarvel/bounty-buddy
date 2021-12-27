package com.radiantmood.bountybuddy

import android.app.Application

lateinit var App: Application
    private set

class BuddyBountyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        App = this
    }
}