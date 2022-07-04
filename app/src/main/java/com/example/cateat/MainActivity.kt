package com.example.cateat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cateat.database.UserDbHelper
import com.example.cateat.exceptions.CatException
import com.example.cateat.service.authentication.LoginRepository
import com.example.cateat.service.common.Result
import com.example.cateat.service.indication.IndicationRepository
import com.example.cateat.ui.login.LoginActivity
import com.example.cateat.utils.CatUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    /**
     * Слой работы с показателями.
     */
    private lateinit var indicationRepository: IndicationRepository

    /**
     * Слой работы с текущим пользователем.
     */
    private lateinit var loginRepository: LoginRepository

    /**
     * Слой работы с локальной базой данных.
     */
    private lateinit var localDbConnection: UserDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        btnRefreshDateTime.setOnClickListener {
            val dateTime = CatUtils.getCurrentFormattedDateTime()
            editTextDate.setText(dateTime.first)
            editTextTime.setText(dateTime.second)
        }

        btnSave.setOnClickListener {
            sendData()
        }

        openLoginActivityIfNeed()
    }

    private fun openLoginActivityIfNeed() {
        val existingUser = localDbConnection.readUserInfo()
        if (existingUser == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        indicationRepository = IndicationRepository(this)
        loginRepository = LoginRepository()
        localDbConnection = UserDbHelper(this)
    }

    private fun getEditValue() : Int {
        return txtNumberEatValue.text.toString().trim().toInt()
    }

    private fun getEditInstant() : String {
        return CatUtils.getFormattedInstant(editTextDate.text.toString(), editTextTime.text.toString())
    }

    private fun clearEditValues() {
        editTextDate.setText("")
        editTextTime.setText("")
        txtNumberEatValue.setText("")
    }

    private fun sendData() {
        try {
            val value = getEditValue()
            val dateTime = getEditInstant()

            val existingUser = localDbConnection.readUserInfo() ?: throw CatException("current user is null")
            val url = getString(R.string.cat_service_login_url)
            runBlocking {
                launch {
                    val loggedInUser = loginRepository.login(url, existingUser.login, existingUser.password)
                    val token = if (loggedInUser is Result.Success) { loggedInUser.data.token } else ""
                    indicationRepository.transferLocalDataToServer(token)
                    indicationRepository.saveData(token, dateTime, value)
                }
                clearEditValues()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}