package com.example.cateat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cateat.database.UserDbHelper
import com.example.cateat.databinding.ActivityMainBinding
import com.example.cateat.exceptions.CatException
import com.example.cateat.service.authentication.LoginRepository
import com.example.cateat.service.common.Result
import com.example.cateat.service.indication.IndicationRepository
import com.example.cateat.ui.login.LoginActivity
import com.example.cateat.utils.CatUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    private lateinit var indicationRepository: IndicationRepository
    private lateinit var loginRepository: LoginRepository
    private lateinit var localDbConnection: UserDbHelper
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivity()

        openLoginActivityIfNeed()
    }

    private fun openLoginActivityIfNeed() {
        val existingUser = localDbConnection.readUserInfo()
        if (existingUser == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initActivity() {
        indicationRepository = IndicationRepository(this)
        loginRepository = LoginRepository()
        localDbConnection = UserDbHelper(this)

        binding.btnRefreshDateTime.setOnClickListener {
            val dateTime = CatUtils.getCurrentFormattedDateTime()
            binding.editTextDate.setText(dateTime.first)
            binding.editTextTime.setText(dateTime.second)
        }

        binding.btnSave.setOnClickListener {
            sendData()
        }
    }

    private fun getEditValue() : Int {
        return binding.txtNumberEatValue.text.toString().trim().toInt()
    }

    private fun getEditInstant() : String {
        return CatUtils.getFormattedInstant(binding.editTextDate.text.toString(), binding.editTextTime.text.toString())
    }

    private fun clearEditValues() {
        binding.editTextDate.setText("")
        binding.editTextTime.setText("")
        binding.txtNumberEatValue.setText("")
    }

    private fun sendData() {
        try {
            val value = getEditValue()
            val dateTime = getEditInstant()

            val existingUser = localDbConnection.readUserInfo() ?: throw CatException("current user is null")
            val url = getString(R.string.cat_service_login_url)
            GlobalScope.launch {
                val loggedInUser = loginRepository.login(url, existingUser.login, existingUser.password)
                val token = if (loggedInUser is Result.Success) { loggedInUser.data.token } else ""
                indicationRepository.transferLocalDataToServer(token)
                indicationRepository.saveData(token, dateTime, value)
            }
            clearEditValues()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}