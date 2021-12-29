package com.radiantmood.bountybuddy.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val prettyPrinter = Json { prettyPrint = true }
inline fun <reified T> T?.toPrettyString(): String = if (this == null) "" else prettyPrinter.encodeToString(this)