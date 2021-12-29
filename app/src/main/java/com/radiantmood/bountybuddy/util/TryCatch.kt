package com.radiantmood.bountybuddy.util

import android.util.Log

inline fun tryLog(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Log.d("araiff", e.message.orEmpty())
    }
}