package com.radiantmood.bountybuddy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.radiantmood.bountybuddy.WorldContentContract.COLUMN_NAME_ID
import com.radiantmood.bountybuddy.WorldContentContract.COLUMN_NAME_JSON
import com.radiantmood.bountybuddy.WorldContentContract.TABLE_NAME

object WorldContentContract {
    const val TABLE_NAME = "DestinyInventoryItemDefinition"
    const val COLUMN_NAME_ID = "id"
    const val COLUMN_NAME_JSON = "json"
}

private const val SQL_CREATE_DIID =
    """
        CREATE TABLE "$TABLE_NAME" (
	       "$COLUMN_NAME_ID"	INTEGER NOT NULL,
	       "$COLUMN_NAME_JSON"	BLOB DEFAULT (null),
	       PRIMARY KEY("$COLUMN_NAME_ID")
        );
    """

private const val SQL_DELETE_DIID = "DROP TABLE IF EXISTS $TABLE_NAME"

class WorldContentDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL(SQL_CREATE_DIID)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE_DIID)
//        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "WorldContent.db"
    }
}