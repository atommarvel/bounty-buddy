package com.radiantmood.bountybuddy.db

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.radiantmood.bountybuddy.App
import com.radiantmood.bountybuddy.data.InventoryItemDefinition
import com.radiantmood.bountybuddy.network.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString

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

    suspend fun getItem(itemId: UInt): InventoryItemDefinition? {
        return getJson(WorldContentContract.TABLE_NAME, itemId)
    }

    private suspend fun getJson(table: String, itemId: UInt): InventoryItemDefinition? = withContext(Dispatchers.IO) {
        // ids provided by the API are unsigned ints, yet the db stores them signed
        val id = itemId.toInt().toString()
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

        val result: InventoryItemDefinition? =
            try {
                if (cursor.moveToNext()) {
                    val string = cursor.getString(cursor.getColumnIndexOrThrow(WorldContentContract.COLUMN_NAME_JSON))
                    RetrofitBuilder.json.decodeFromString(string)
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