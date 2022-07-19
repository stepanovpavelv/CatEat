package com.example.cateat.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Работа созданием/удалением таблиц БД.
 */
open class CommonDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 4) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_USER_TABLE_SCRIPT)
        db?.execSQL(CREATE_INDICATIONS_TABLE_SCRIPT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_USER_TABLE_SCRIPT)
        db?.execSQL(DROP_INDICATIONS_TABLE_SCRIPT)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME: String = "CatEat"
        const val USER_TABLE: String = "users"
        const val USERNAME_FIELD: String = "username"
        const val PASSWORD_FIELD: String = "password"

        const val INDICATIONS_TABLE: String = "indications"
        const val INDICATIONS_ID_FIELD: String = "id"
        const val INDICATIONS_DATE_FIELD: String = "date"
        const val INDICATIONS_VALUE_FIELD: String = "value"

        private const val CREATE_INDICATIONS_TABLE_SCRIPT: String = """CREATE TABLE IF NOT EXISTS $INDICATIONS_TABLE (
                $INDICATIONS_ID_FIELD INTEGER PRIMARY KEY AUTOINCREMENT,
                $INDICATIONS_DATE_FIELD TEXT,
                $INDICATIONS_VALUE_FIELD INTEGER
            );"""

        private const val CREATE_USER_TABLE_SCRIPT: String = """CREATE TABLE IF NOT EXISTS $USER_TABLE (
                $USERNAME_FIELD TEXT PRIMARY KEY,
                $PASSWORD_FIELD TEXT
            );"""

        private const val DROP_INDICATIONS_TABLE_SCRIPT: String = "DROP TABLE IF EXISTS $INDICATIONS_TABLE;"

        private const val DROP_USER_TABLE_SCRIPT: String = "DROP TABLE IF EXISTS $USER_TABLE;"
    }
}