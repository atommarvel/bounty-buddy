package com.radiantmood.bountybuddy.auth

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Calendar

@Serializable
data class AuthState(
    val code: String? = null,
    val token: String? = null,
    @Serializable(CalendarSerializer::class)
    val tokenExpiry: Calendar? = null,
) {
    fun isTokenExpired(): Boolean = tokenExpiry?.let { Calendar.getInstance().after(it) } ?: true
}

object CalendarSerializer : KSerializer<Calendar> {
    override val descriptor = PrimitiveSerialDescriptor("Calendar", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Calendar) = encoder.encodeLong(value.timeInMillis)
    override fun deserialize(decoder: Decoder): Calendar = Calendar.getInstance().apply {
        timeInMillis = decoder.decodeLong()
    }
}