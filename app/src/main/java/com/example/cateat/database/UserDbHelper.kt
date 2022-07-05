package com.example.cateat.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import com.example.cateat.service.authentication.model.UserLoginDto

/**
 * Работа с локальной БД - таблица "Пользователи".
 */
class UserDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 3) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_SCRIPT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE_SCRIPT)
        onCreate(db)
    }

    fun addUserInfo(dto: UserLoginDto) {
        val contentValues = contentValuesOf(
            USERNAME_FIELD to dto.login,
            PASSWORD_FIELD to dto.password
        )

        val db = this.writableDatabase
        db.insert(TABLE_NAME,null, contentValues)
        db.close()
    }

    @SuppressLint("Range")
    fun readUserInfo() : UserLoginDto? {
        var dbUser: UserLoginDto? = null

        val database = this.readableDatabase
        val cursor = openCursor(database, TABLE_NAME)

        if (cursor.moveToNext()) {
            dbUser = UserLoginDto(
                cursor.getString(cursor.getColumnIndex(USERNAME_FIELD)),
                cursor.getString(cursor.getColumnIndex(PASSWORD_FIELD))
            )
        }

        cursor.close()
        database.close()

        return dbUser
    }

    private fun openCursor(db: SQLiteDatabase, tableName: String): Cursor {
        val fields = arrayOf(USERNAME_FIELD, PASSWORD_FIELD)
        return db.query(tableName, fields, null, null, null, null, USERNAME_FIELD)
    }

    companion object {
        private const val DATABASE_NAME: String = "CatEat"
        private const val TABLE_NAME: String = "users"
        private const val USERNAME_FIELD: String = "username"
        private const val PASSWORD_FIELD: String = "password"

        private const val CREATE_TABLE_SCRIPT: String = """CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $USERNAME_FIELD TEXT PRIMARY KEY,
                $PASSWORD_FIELD TEXT
            );"""

        private const val DROP_TABLE_SCRIPT: String = "DROP TABLE IF EXISTS $TABLE_NAME;"

        private const val DELETE_RECORDS_SCRIPT: String = "DELETE FROM $TABLE_NAME;"

        private const val DELETE_ONE_RECORD_SCRIPT: String = "$DELETE_RECORDS_SCRIPT WHERE $USERNAME_FIELD"
    }
}