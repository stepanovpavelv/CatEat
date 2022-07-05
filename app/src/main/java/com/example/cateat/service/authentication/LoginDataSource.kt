package com.example.cateat.service.authentication

import com.example.cateat.exceptions.CatException
import com.example.cateat.service.authentication.model.LoggedInUser
import com.example.cateat.service.authentication.model.UserLoginDto
import com.example.cateat.service.authentication.model.UserTokenDto
import com.example.cateat.service.common.RestClient
import com.example.cateat.service.common.Result
import com.example.cateat.utils.CatUtils
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.io.IOException
import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(loginUrl: String, username: String, password: String): Result<LoggedInUser> {
        val dtoInfo = UserLoginDto(username, password)
        try {
            val response = RestClient.http.post(loginUrl) {
                contentType(ContentType.Application.Json)
                setBody(dtoInfo)
            }

            if (response.status != HttpStatusCode.OK) {
                throw CatException(CatUtils.SERVER_NOT_RESPOND_MESSAGE)
            }

            val tokenDto: UserTokenDto = response.body()

            // TODO: handle loggedInUser authentication
            val realUser = LoggedInUser(UUID.randomUUID().toString(), username, tokenDto.token)
            return Result.Success(realUser)

        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}