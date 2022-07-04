package com.example.cateat.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import com.example.cateat.service.indication.CatSavedInfoDto

/**
 * Работа с локальной БД - таблица "Показатели".
 */
class CatDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_SCRIPT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE_SCRIPT)
        onCreate(db)
    }

    fun addRecord(dto: CatSavedInfoDto) {
        val contentValues = contentValuesOf(
            DATE_FIELD to dto.date,
            VALUE_FIELD to dto.value
        )

        val db = this.writableDatabase
        db.insert(TABLE_NAME,null, contentValues)
        db.close()
    }

    @SuppressLint("Range")
    fun readRecords(): List<CatSavedInfoDto> {
        val result = mutableListOf<CatSavedInfoDto>()

        val database = this.readableDatabase
        val cursor = openCursor(database, TABLE_NAME)

        while (cursor.moveToNext()) {
            result.add(CatSavedInfoDto(
                date = cursor.getString(cursor.getColumnIndex(DATE_FIELD)),
                value = cursor.getInt(cursor.getColumnIndex(VALUE_FIELD)),
            ))
        }

        cursor.close()
        database.close()

        return listOf(*(result.toTypedArray()))
    }

    fun deleteRecord(dto: CatSavedInfoDto) {
        val db = this.writableDatabase
        db.execSQL("$DELETE_ONE_RECORD_SCRIPT=${dto.date}")
        db.close()
    }

    fun clearTable() {
        val db = this.writableDatabase
        db.execSQL(DELETE_RECORDS_SCRIPT)
        db.close()
    }

    private fun openCursor(db: SQLiteDatabase, tableName: String): Cursor {
        val fields = arrayOf(DATE_FIELD, VALUE_FIELD)
        return db.query(tableName, fields, null, null, null, null, ID_FIELD)
    }

    companion object {
        private const val DATABASE_NAME: String = "CatEat"
        private const val TABLE_NAME: String = "indications"
        private const val ID_FIELD: String = "id"
        private const val DATE_FIELD: String = "date"
        private const val VALUE_FIELD: String = "value"

        private const val CREATE_TABLE_SCRIPT: String = """CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $ID_FIELD INTEGER PRIMARY KEY AUTOINCREMENT,
                $DATE_FIELD TEXT,
                $VALUE_FIELD INTEGER
            );"""

        private const val DROP_TABLE_SCRIPT: String = "DROP TABLE IF EXISTS $TABLE_NAME;"

        private const val DELETE_RECORDS_SCRIPT: String = "DELETE FROM $TABLE_NAME;"

        private const val DELETE_ONE_RECORD_SCRIPT: String = "$DELETE_RECORDS_SCRIPT WHERE $DATE_FIELD"
    }
}