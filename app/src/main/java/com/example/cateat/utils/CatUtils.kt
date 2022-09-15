package com.example.cateat.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Вспомогательные утилитки.
 */
class CatUtils {

    companion object {
        private const val SERVER_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX"
        private const val DATE_PATTERN = "dd.MM.yyyy"
        private const val TIME_PATTERN = "HH:mm"

        const val SERVER_NOT_RESPOND_MESSAGE = "Сервер не отвечает"

        fun getDateByRegistryTicks(seconds: Long) : String {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.of("UTC"))
                .atZone(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
        }

        fun getRegistryDateTicks(serverDate: String) : Long {
            val sourceFormatter = DateTimeFormatter.ofPattern(SERVER_DATETIME_FORMAT)

            return LocalDateTime.parse(serverDate, sourceFormatter)
                .atZone(ZoneOffset.UTC)
                .toEpochSecond()
        }

        fun getRegistryISODate(serverDate: String) : String {
            val sourceFormatter = DateTimeFormatter.ofPattern(SERVER_DATETIME_FORMAT)
            val targetFormatter = DateTimeFormatter.ofPattern("$DATE_PATTERN $TIME_PATTERN")
            return LocalDateTime.parse(serverDate, sourceFormatter)
                .atZone(ZoneOffset.UTC)
                .format(targetFormatter)
        }

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