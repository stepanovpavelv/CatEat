package com.example.cateat.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.cateat.R
import com.example.cateat.database.UserDbHelper
import com.example.cateat.databinding.ActivityRegistryBinding
import com.example.cateat.exceptions.CatException
import com.example.cateat.service.authentication.LoginRepository
import com.example.cateat.service.common.Result
import com.example.cateat.service.registry.RegistryItemDto
import com.example.cateat.service.registry.RegistryRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RegistryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistryBinding
    private lateinit var localUserDbHelper: UserDbHelper
    private lateinit var loginRepository: LoginRepository
    private lateinit var registryRepository: RegistryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivity()
        clearRegistryTable()
        initRegistryTable()
    }

    private fun initActivity() {
        localUserDbHelper = UserDbHelper(this)
        loginRepository = LoginRepository()
        registryRepository = RegistryRepository(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnBack.setOnLongClickListener {
            registryRepository.clear()
            true
        }
    }

    private fun clearRegistryTable() {
        binding.regTable.removeAllViews()
    }

    private fun initRegistryTable() {
        val receivedItems = getRecordsView()
        //val receivedItems = arrayOf<RegistryItemDto>()

        var numberId = 0
        // header
        val headerRow = TableRow(this)
        headerRow.id = numberId++
        headerRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        )

        val firstHeaderColumn = TextView(this)
        firstHeaderColumn.id = numberId++

        firstHeaderColumn.setBackgroundResource(R.color.RegistryBackgroundHeader)
        firstHeaderColumn.gravity = Gravity.CENTER_HORIZONTAL
        firstHeaderColumn.setPadding(10)
        firstHeaderColumn.text = getString(R.string.RegistryNumberHead)
        firstHeaderColumn.setTextColor(Color.WHITE)
        firstHeaderColumn.textSize = 14f

        val secondHeaderColumn = TextView(this)
        secondHeaderColumn.id = numberId++

        secondHeaderColumn.setBackgroundResource(R.color.RegistryBackgroundHeader)
        secondHeaderColumn.gravity = Gravity.CENTER_HORIZONTAL
        secondHeaderColumn.setPadding(10)
        secondHeaderColumn.text = getString(R.string.RegistryDateHead)
        secondHeaderColumn.setTextColor(Color.WHITE)
        secondHeaderColumn.textSize = 14f

        val thirdHeaderColumn = TextView(this)
        thirdHeaderColumn.id = numberId++

        thirdHeaderColumn.setBackgroundResource(R.color.RegistryBackgroundHeader)
        thirdHeaderColumn.gravity = Gravity.CENTER_HORIZONTAL
        thirdHeaderColumn.setPadding(10)
        thirdHeaderColumn.text = getString(R.string.RegistryValueHead)
        thirdHeaderColumn.setTextColor(Color.WHITE)
        thirdHeaderColumn.textSize = 14f

        headerRow.addView(firstHeaderColumn, TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        ))
        headerRow.addView(secondHeaderColumn, TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        ))
        headerRow.addView(thirdHeaderColumn, TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT
        ))
        binding.regTable.addView(headerRow)

        if (receivedItems.isEmpty()) {
            return
        }

        for (it in receivedItems) {
            val tableRow = TableRow(this)
            tableRow.id = numberId++
            tableRow.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )

            val textNumber = TextView(this)
            textNumber.id = numberId++
            textNumber.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT,
                2.0f
            )
            textNumber.setBackgroundColor(Color.WHITE)
            textNumber.gravity = Gravity.CENTER_HORIZONTAL
            textNumber.setPadding(10)
            textNumber.text = it.number.toString()
            textNumber.setTextColor(Color.BLACK)
            textNumber.textSize = 14f

            val textDate = TextView(this)
            textDate.id = numberId++
            textDate.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT,
                4.0f
            )
            textDate.setBackgroundColor(Color.WHITE)
            textDate.gravity = Gravity.CENTER_HORIZONTAL
            textDate.setPadding(10)
            textDate.text = it.date
            textDate.setTextColor(Color.BLACK)
            textDate.textSize = 14f

            val textValue = TextView(this)
            textValue.id = numberId++
            textValue.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT,
                2.0f
            )
            textValue.setBackgroundColor(Color.WHITE)
            textValue.gravity = Gravity.CENTER_HORIZONTAL
            textValue.setPadding(10)
            textValue.text = it.value.toString()
            textValue.setTextColor(Color.BLACK)
            textValue.textSize = 14f

            tableRow.addView(textNumber, TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            ))
            tableRow.addView(textDate, TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            ))
            tableRow.addView(textValue, TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            ))

            binding.regTable.addView(tableRow)
        }
    }

    private fun getRecordsView() : List<RegistryItemDto> {
        var serverRecords: List<RegistryItemDto> = listOf()
        val existingUser = localUserDbHelper.readUserInfo() ?: throw CatException("current user is null")
        val url = getString(R.string.cat_service_login_url)

        runBlocking {
            launch {
                val loggedInUser = loginRepository.login(url, existingUser.login, existingUser.password)
                val token = if (loggedInUser is Result.Success) { loggedInUser.data.token } else ""
                serverRecords = registryRepository.saveAndGetClientRecords(token)
            }
        }

        return serverRecords.reversed()
    }
}