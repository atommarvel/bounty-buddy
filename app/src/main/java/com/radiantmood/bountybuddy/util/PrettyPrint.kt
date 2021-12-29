package com.radiantmood.bountybuddy.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val prettyPrinter = Json { prettyPrint = true }

/**
 * Note: doesn't protect itself from being used by a class not marked with [Serializable] annotation.
 */
@Throws(Exception::class)
inline fun <reified T> T?.toPrettyString(): String = if (this == null) "" else prettyPrinter.encodeToString(this)