package com.ivyclub.contact.util

import android.content.Context
import android.content.res.AssetManager
import com.ivyclub.data.ContactRepository
import com.ivyclub.data.model.PlanData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.sql.Date
import javax.inject.Inject

class TestPlanDataManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ContactRepository
) {

    suspend fun insertTestPlanData() = withContext(Dispatchers.IO) {
        val assetManager = context.resources.assets
        try {
            val inputStream = assetManager.open("planData_test.csv", AssetManager.ACCESS_BUFFER)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine()
            var line: String? = reader.readLine()
            while (line != null) {
                line.split(",").also {
                    val date = Date(BigDecimal(it[1]).longValueExact())
                    repository.savePlanData(
                        PlanData(
                            participant = emptyList(),
                            date = date,
                            title = it[2],
                            place = it[3],
                            content = it[4],
                            color = it[5],
                            id = it[6].toLong()
                        )
                    )
                }
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}