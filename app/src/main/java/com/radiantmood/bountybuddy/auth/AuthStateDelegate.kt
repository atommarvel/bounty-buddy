package com.radiantmood.bountybuddy.auth

import android.content.Context
import android.content.SharedPreferences
import com.radiantmood.bountybuddy.App
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KProperty

/**
 * Delegate that writes [AuthState] to shared prefs any time the value has its value set.
 */
class AuthStateDelegate {
    private var _authState: AuthState? = null
    private val prefs: SharedPreferences get() = App.getSharedPreferences("auth", Context.MODE_PRIVATE)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): AuthState {
        if (_authState == null) {
            _authState = readAuthState()
        }
        return _authState!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: AuthState) {
        writeAuthState(value)
        _authState = value
    }

    private fun readAuthState(): AuthState {
        val jsonState = prefs.getString(PREF_KEY_AUTH_STATE, null)
        return if (jsonState != null) {
            Json.decodeFromString(jsonState)
        } else AuthState()
    }

    private fun writeAuthState(state: AuthState) {
        prefs.edit()
            .putString(PREF_KEY_AUTH_STATE, Json.encodeToString(state))
            .apply()
    }

    companion object {
        const val PREF_KEY_AUTH_STATE = "authState"
    }
}