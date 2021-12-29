package com.radiantmood.bountybuddy.util

import android.util.Log

inline fun tryLog(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Log.e("araiff", e.message, e)
    }
}