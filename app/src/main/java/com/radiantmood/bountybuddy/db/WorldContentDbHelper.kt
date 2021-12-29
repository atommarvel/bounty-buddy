package com.radiantmood.bountybuddy.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

object WorldContentContract {
    const val TABLE_NAME = "DestinyInventoryItemDefinition"

    const val COLUMN_NAME_ID = "id"
    const val COLUMN_NAME_JSON = "json"

    val projection by lazy { arrayOf(COLUMN_NAME_ID, COLUMN_NAME_JSON) }
}

/**
 * overrides are no-op because the db is downloaded from the internet
 * powered by: https://developer.android.com/training/data-storage/sqlite
 */
class WorldContentDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) { /*no-op*/
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { /*no-op*/
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "WorldContent.db"
    }
}