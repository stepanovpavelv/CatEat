package com.example.cateat.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Вспомогательные утилитки.
 */
class CatUtils {

    companion object {
        private const val DATE_PATTERN = "dd.MM.yyyy"
        private const val TIME_PATTERN = "HH:mm"

        const val SERVER_NOT_RESPOND_MESSAGE = "Сервер не отвечает"

        fun getFormattedInstant(date: String, time: String) : String {
            return LocalDateTime.parse(
                "${date.trim()} ${time.trim()}",
                DateTimeFormatter.ofPattern("$DATE_PATTERN $TIME_PATTERN")
            )
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT)
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentFormattedDateTime() : Pair<String, String> {
            val calendar = Calendar.getInstance()
            val date = SimpleDateFormat(DATE_PATTERN).format(calendar.time)
            val time = SimpleDateFormat(TIME_PATTERN).format(calendar.time)
            return Pair(date, time)
        }
    }
}