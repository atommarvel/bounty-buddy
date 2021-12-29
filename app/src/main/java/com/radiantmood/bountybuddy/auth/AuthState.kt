package com.radiantmood.bountybuddy.auth

import com.radiantmood.bountybuddy.util.CalendarSerializer
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class AuthState(
    val code: String? = null,
    val token: String? = null,
    @Serializable(CalendarSerializer::class)
    val tokenExpiry: Calendar? = null,
    val membershipId: String? = null,
) {
    fun isTokenExpired(): Boolean = tokenExpiry?.let { Calendar.getInstance().after(it) } ?: true
}