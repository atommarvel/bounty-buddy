package com.radiantmood.bountybuddy.dev

import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import com.radiantmood.bountybuddy.App
import okhttp3.OkHttpClient

/**
 * Facebook Flipper integration
 */
class Devtool {

    private val networkPlugin: NetworkFlipperPlugin = NetworkFlipperPlugin()

    init {
        SoLoader.init(App, false)
        AndroidFlipperClient.getInstance(App).apply {
            addPlugin(networkPlugin)
            start()
        }
    }

    fun addNetworkInterceptor(builder: OkHttpClient.Builder) {
        builder.addNetworkInterceptor(FlipperOkhttpInterceptor(networkPlugin))
    }
}