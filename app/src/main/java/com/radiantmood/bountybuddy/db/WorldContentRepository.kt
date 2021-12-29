package com.radiantmood.bountybuddy.db

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.radiantmood.bountybuddy.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class WorldContentRepository {
    private var _db: SQLiteDatabase? = null
    private suspend fun getDb(): SQLiteDatabase {
        if (_db == null) {
            withContext(Dispatchers.IO) {
                _db = WorldContentDbHelper(App).readableDatabase
            }
        }
        return checkNotNull(_db)
    }

    suspend fun getItem(itemId: String): JsonElement? {
        return getJson(WorldContentContract.TABLE_NAME, itemId)
    }

    private suspend fun getJson(table: String, itemId: String): JsonElement? = withContext(Dispatchers.IO) {
        // ids provided by the API are unsigned ints, yet the db stores them signed
        val id = itemId.toUInt().toInt().toString()
        val selection = "${WorldContentContract.COLUMN_NAME_ID} = ?"
        val selectionArgs = arrayOf(id)
        val cursor = getDb().query(
            table,
            WorldContentContract.projection,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )

        val lenientJson = Json { isLenient = true }

        val result: JsonElement? =
            try {
                if (cursor.moveToNext()) {
                    val string = cursor.getString(cursor.getColumnIndexOrThrow(WorldContentContract.COLUMN_NAME_JSON))
                    lenientJson.decodeFromString(string)
                } else null
            } catch (e: Exception) {
                Log.e("araiff", e.message, e)
                null
            } finally {
                cursor.close()
            }

        return@withContext result
    }
}