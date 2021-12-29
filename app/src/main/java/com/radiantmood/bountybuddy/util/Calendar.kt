package com.radiantmood.bountybuddy.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Calendar

fun calendarIn(futureSeconds: Int): Calendar =
    Calendar.getInstance().apply {
        add(Calendar.SECOND, futureSeconds)
    }

object CalendarSerializer : KSerializer<Calendar> {
    override val descriptor = PrimitiveSerialDescriptor("Calendar", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Calendar) = encoder.encodeLong(value.timeInMillis)
    override fun deserialize(decoder: Decoder): Calendar = Calendar.getInstance().apply {
        timeInMillis = decoder.decodeLong()
    }
}