package com.example.cateat.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import com.example.cateat.service.indication.CatSavedInfoDto

/**
 * Работа с локальной БД - таблица "Показатели".
 */
class CatDbHelper(context: Context) : CommonDbHelper(context) {

    fun addRecord(dto: CatSavedInfoDto) {
        val contentValues = contentValuesOf(
            INDICATIONS_DATE_FIELD to dto.date,
            INDICATIONS_VALUE_FIELD to dto.value
        )

        val db = this.writableDatabase
        db.insert(INDICATIONS_TABLE,null, contentValues)
        db.close()
    }

    @SuppressLint("Range")
    fun readRecords(): List<CatSavedInfoDto> {
        val result = mutableListOf<CatSavedInfoDto>()

        val database = this.readableDatabase
        val cursor = openCursor(database, INDICATIONS_TABLE)

        while (cursor.moveToNext()) {
            result.add(CatSavedInfoDto(
                date = cursor.getString(cursor.getColumnIndex(INDICATIONS_DATE_FIELD)),
                value = cursor.getInt(cursor.getColumnIndex(INDICATIONS_VALUE_FIELD)),
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
        val fields = arrayOf(INDICATIONS_DATE_FIELD, INDICATIONS_VALUE_FIELD)
        return db.query(tableName, fields, null, null, null, null, INDICATIONS_ID_FIELD)
    }

    companion object {

        private const val DELETE_RECORDS_SCRIPT: String = "DELETE FROM $INDICATIONS_TABLE;"

        private const val DELETE_ONE_RECORD_SCRIPT: String = "$DELETE_RECORDS_SCRIPT WHERE $INDICATIONS_DATE_FIELD"
    }
}