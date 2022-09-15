package com.example.cateat.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.contentValuesOf
import com.example.cateat.service.registry.IndicationItemDto
import com.example.cateat.service.registry.RegistryItemDto
import com.example.cateat.utils.CatUtils
import java.time.LocalDateTime

/**
 * Работа с локальной БД - таблица "Реестр показателей".
 */
class RegistryDbHelper(context: Context) : CommonDbHelper(context) {
    fun addRecords(items: List<IndicationItemDto>) {
        if (items.isEmpty()) {
            return
        }

        var number = maxRecordNumber() + 1

        val db = this.writableDatabase
        try {
            db.beginTransaction()

            items.forEach {
                val dbContent = contentValuesOf(
                    REGISTRY_NUMBER_FIELD to number++,
                    REGISTRY_DATE_FIELD to it.date,
                    REGISTRY_DATETICKS_FIELD to CatUtils.getRegistryDateTicks(it.date),
                    REGISTRY_VALUE_FIELD to it.value
                )
                db.insert(REGISTRY_TABLE, null, dbContent)
            }

            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    @SuppressLint("Range")
    fun readRecords() : List<RegistryItemDto> {
        val db = this.readableDatabase

        val fields = arrayOf(REGISTRY_NUMBER_FIELD, REGISTRY_DATE_FIELD, REGISTRY_VALUE_FIELD)
        val cursor = openCursor(db, REGISTRY_TABLE, fields, null)

        val result = mutableListOf<RegistryItemDto>()
        while (cursor.moveToNext()) {
            result.add(
                RegistryItemDto(
                    cursor.getInt(cursor.getColumnIndex(REGISTRY_NUMBER_FIELD)),
                    CatUtils.getRegistryISODate(cursor.getString(cursor.getColumnIndex(REGISTRY_DATE_FIELD))),
                    cursor.getInt(cursor.getColumnIndex(REGISTRY_VALUE_FIELD))
                )
            )
        }

        cursor.close()
        db.close()

        return result
    }

    @SuppressLint("Range")
    fun maxDate(): Long {
        val db = this.readableDatabase

        val cursor = openCursor(db, REGISTRY_TABLE, arrayOf("MAX($REGISTRY_DATETICKS_FIELD) AS max_ticks") , null)

        var maxTicks = -1L
        if (cursor.moveToNext()) {
            maxTicks = cursor.getLong(cursor.getColumnIndex("max_ticks"))
        }

        cursor.close()
        db.close()

        return maxTicks
    }

    fun clearTable() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $REGISTRY_TABLE;")

        db.close()
    }

    @SuppressLint("Range")
    private fun maxRecordNumber() : Int {
        val db = this.readableDatabase

        val cursor = openCursor(db, REGISTRY_TABLE, arrayOf("MAX($REGISTRY_NUMBER_FIELD) AS max_number"), null)

        var maxNumber = 0
        if (cursor.moveToNext()) {
            maxNumber = cursor.getInt(cursor.getColumnIndex("max_number"))
        }

        cursor.close()
        db.close()

        return maxNumber
    }
}