package com.radiantmood.bountybuddy.auth

import com.radiantmood.bountybuddy.App
import com.radiantmood.bountybuddy.R
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${App.authManager.authState.token}")
            .addHeader("X-API-KEY", App.getString(R.string.api_key))
            .let { chain.proceed(it.build()) }
    }
}