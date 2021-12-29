package com.radiantmood.bountybuddy

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

class ManifestVersionDelegate {
    private var _version: String? = null
    private val prefs: SharedPreferences get() = App.getSharedPreferences("bountybuddy", Context.MODE_PRIVATE)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        if (_version == null) {
            _version = readVersion()
        }
        return _version!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        writeVersion(value)
        _version = value
    }

    private fun readVersion(): String {
        return prefs.getString(PREF_KEY_VERSION, null) ?: "none"
    }

    private fun writeVersion(version: String) {
        prefs.edit()
            .putString(PREF_KEY_VERSION, version)
            .apply()
    }

    companion object {
        const val PREF_KEY_VERSION = "version"
    }
}