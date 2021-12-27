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
    private val json = Json { ignoreUnknownKeys = true }

    val authClient by lazy {
        val builder = OkHttpClient.Builder()
        App.devtool.addNetworkInterceptor(builder)
        builder.build()
    }

    val client by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
        App.devtool.addNetworkInterceptor(builder)
        builder.build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val bungieService: BungieService = retrofit.create(BungieService::class.java)

    private const val BASE_URL = "https://www.bungie.net/Platform/"
}