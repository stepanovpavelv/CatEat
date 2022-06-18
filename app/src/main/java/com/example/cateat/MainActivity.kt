package com.example.cateat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cateat.database.CatDbHelper
import com.example.cateat.utils.CatUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    /**
     * http-клиент для взаимодействия с сервисом.
     */
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }
        install(ContentNegotiation) {
            json()
        }
    }

    /**
     * Слой работы с БД.
     */
    private lateinit var dbConnection: CatDbHelper

    /**
     * url для создания элемента.
     */
    private lateinit var createItemUrl: String

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        btnRefreshDateTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            editTextDate.setText(SimpleDateFormat(CatUtils.DATE_PATTERN).format(calendar.time))
            editTextTime.setText(SimpleDateFormat(CatUtils.TIME_PATTERN).format(calendar.time))
        }

        btnSave.setOnClickListener {
            try {
                val value = getEditValue()
                val dateTime = getEditInstant()

                runBlocking {
                    launch {
                        transferLocalDataToServer()
                        saveData(dateTime, value)
                    }
                    clearEditValues()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun init() {
        dbConnection = CatDbHelper(this)
        createItemUrl = this.getString(R.string.cat_service_create_url)
    }

    private fun getEditValue() : Int {
        return txtNumberEatValue.text.toString().trim().toInt()
    }

    private fun getEditInstant() : String {
        return LocalDateTime.parse(
            "${editTextDate.text.trim()} ${editTextTime.text.trim()}",
            DateTimeFormatter.ofPattern("${CatUtils.DATE_PATTERN} ${CatUtils.TIME_PATTERN}")
        )
        .atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT)
    }

    private fun clearEditValues() {
        editTextDate.setText("")
        editTextTime.setText("")
        txtNumberEatValue.setText("")
    }

    private suspend fun saveData(date: String, value: Int) {
        val dtoInfo = CatSavedInfoDto(date, value)

        try {
            sendSaveRequest(dtoInfo)
        } catch (ex: Exception) {
            dbConnection.addRecord(dtoInfo)
        }
    }

    private suspend fun sendSaveRequest(dtoInfo: CatSavedInfoDto) {
        val createUrl = this.getString(R.string.cat_service_create_url)
        val response: HttpResponse = client.post(createUrl) {
            contentType(ContentType.Application.Json)
            setBody(dtoInfo)
        }
    }

    private suspend fun transferLocalDataToServer() {
        val records = dbConnection.readRecords()
        if (records.isEmpty()) {
            return
        }

        try {
            records.forEach {
                sendSaveRequest(it)
                dbConnection.deleteRecord(it)
            }
        }
        catch (ex: Exception) { } // do nothing
    }
}