package com.example.cateat.service.indication

import android.content.Context
import com.example.cateat.R
import com.example.cateat.database.CatDbHelper
import com.example.cateat.exceptions.CatException
import com.example.cateat.service.common.RestClient
import com.example.cateat.utils.CatUtils
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Репозиторий взаимодействия по показаниям с rest-сервисом.
 */
class IndicationRepository(context: Context) {
    /**
     * репозиторий по работе с локальной БД.
     */
    private val localDbConnection: CatDbHelper = CatDbHelper(context)

    /**
     * url для создания элемента.
     */
    private val createItemUrl: String = context.getString(R.string.cat_service_create_url)

    suspend fun saveData(token: String, date: String, value: Int) {
        val dtoInfo = CatSavedInfoDto(date, value)

        try {
            sendSaveRequest(token, dtoInfo)
        } catch (ex: Exception) {
            localDbConnection.addRecord(dtoInfo)
        }
    }

    suspend fun transferLocalDataToServer(token: String) {
        val records = localDbConnection.readRecords()
        if (records.isEmpty()) {
            return
        }

        try {
            records.forEach {
                sendSaveRequest(token, it)
                localDbConnection.deleteRecord(it)
            }
        }
        catch (ex: Exception) { } // do nothing
    }

    private suspend fun sendSaveRequest(token: String, dtoInfo: CatSavedInfoDto) {
        val response: HttpResponse = RestClient.http.post(createItemUrl) {
            contentType(ContentType.Application.Json)
            setBody(dtoInfo)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        if (response.status != HttpStatusCode.OK) {
            throw CatException(CatUtils.SERVER_NOT_RESPOND_MESSAGE)
        }
    }
}