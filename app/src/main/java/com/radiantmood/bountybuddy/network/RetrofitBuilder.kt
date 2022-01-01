package com.radiantmood.bountybuddy.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.radiantmood.bountybuddy.App
import com.radiantmood.bountybuddy.auth.AuthInterceptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitBuilder {
    private const val BASE_URL = "https://www.bungie.net/"
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    /**
     * For making auth token calls (auth headers make those calls fail).
     */
    val authClient by lazy {
        val builder = OkHttpClient.Builder()
        App.devtool.addNetworkInterceptor(builder)
        builder.build()
    }

    private val bungieClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
        App.devtool.addNetworkInterceptor(builder)
        builder.build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .client(bungieClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val bungieService: BungieService = retrofit.create(BungieService::class.java)
}