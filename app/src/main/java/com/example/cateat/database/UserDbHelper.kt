package com.example.cateat.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import com.example.cateat.service.authentication.model.UserLoginDto

/**
 * Работа с локальной БД - таблица "Пользователи".
 */
class UserDbHelper(context: Context) : CommonDbHelper(context) {

    fun addUserInfo(dto: UserLoginDto) {
        val contentValues = contentValuesOf(
            USERNAME_FIELD to dto.login,
            PASSWORD_FIELD to dto.password
        )

        val db = this.writableDatabase
        db.insert(USER_TABLE,null, contentValues)
        db.close()
    }

    @SuppressLint("Range")
    fun readUserInfo() : UserLoginDto? {
        var dbUser: UserLoginDto? = null

        val database = this.readableDatabase
        val cursor = openCursor(
            database,
            USER_TABLE,
            arrayOf(USERNAME_FIELD, PASSWORD_FIELD),
            USERNAME_FIELD
        )

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

    companion object {

        private const val DELETE_RECORDS_SCRIPT: String = "DELETE FROM $USER_TABLE;"

        private const val DELETE_ONE_RECORD_SCRIPT: String = "$DELETE_RECORDS_SCRIPT WHERE $USERNAME_FIELD"
    }
}