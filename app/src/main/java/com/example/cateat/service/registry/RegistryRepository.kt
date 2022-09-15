package com.example.cateat.service.registry

import android.content.Context
import com.example.cateat.R
import com.example.cateat.database.RegistryDbHelper
import com.example.cateat.exceptions.CatException
import com.example.cateat.service.common.RestClient
import com.example.cateat.utils.CatUtils
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Репозиторий взаимодействия по реестру с rest-сервисом.
 */
//@OptIn(DelicateCoroutinesApi::class)
class RegistryRepository(val context: Context) {
    /**
     * репозиторий по работе с локальной БД.
     */
    private val localDbConnection: RegistryDbHelper = RegistryDbHelper(context)

    /**
     * url для создания элемента.
     */
    private val getItemsUrl: String = context.getString(R.string.cat_service_entries_url)

    suspend fun saveAndGetClientRecords(token: String) : List<RegistryItemDto> {
        val registryItems = mutableListOf(*(localDbConnection.readRecords()).toTypedArray())

        var serverRecords = listOf<IndicationItemDto>()
        try {
            serverRecords = if (registryItems.isNotEmpty()) {
                val maxLocalDate = localDbConnection.maxDate()
                val minSearchDate = maxLocalDate + 60L
                getServerRecords(token, CatUtils.getDateByRegistryTicks(minSearchDate), Instant.now().plusSeconds(86400).toString())
            } else {
                getFullServerRecords(token)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        localDbConnection.addRecords(serverRecords) // TODO: вот не по solid-у

        if (serverRecords.isNotEmpty()) {
            registryItems.addAll(serverRecords.mapIndexed { index, it ->
                RegistryItemDto(registryItems.size + index + 1, CatUtils.getRegistryISODate(it.date), it.value)
            })
        }

//        if (registryItems.isEmpty()) {
//            val serverRecords = getAndSaveRecordsFromServer(token, registryItems.isNotEmpty())
//            registryItems.addAll(serverRecords)
//        } else {
//            GlobalScope.launch {
//                getAndSaveRecordsFromServer(token, registryItems.isNotEmpty())
//            }
//        }

        return listOf(*(registryItems.toTypedArray()))
    }

    fun clear() {
        localDbConnection.clearTable()
    }

    //private suspend fun getAndSaveRecordsFromServer(token: String, isNotEmptyRegistry: Boolean) : List<RegistryItemDto> {
    //
    //}

    private suspend fun getFullServerRecords(token: String) : List<IndicationItemDto> {
        return getServerRecords(token, START_DATE.toString(), Instant.now().toString())
    }

    private suspend fun getServerRecords(token: String, start: String, end: String) : List<IndicationItemDto> {
        val dtoInfo = RegistryRequestDto(start, end)
        return sendGetEntriesRequest(token, dtoInfo)
    }

    private suspend fun sendGetEntriesRequest(token: String, dtoRequest: RegistryRequestDto): List<IndicationItemDto> {
        val response: HttpResponse = RestClient.http.post(getItemsUrl) {
            contentType(ContentType.Application.Json)
            setBody(dtoRequest)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        if (response.status != HttpStatusCode.OK) {
            throw CatException(CatUtils.SERVER_NOT_RESPOND_MESSAGE)
        }
        return response.body() as List<IndicationItemDto>
    }

    companion object {
        private val START_DATE: Instant = Instant.now().minusSeconds(5 * 365 * 86400)
    }
}