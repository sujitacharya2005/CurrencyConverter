package com.android.currencies

import android.app.Application
import com.android.currencies.data.local.CurrencyConverterDatabase
import com.android.currencies.data.remote.CurrencyConverterService
import com.android.currencies.data.remote.RetrofitHelper
import com.android.currencies.repository.CurrencyConverterRepository
import com.android.currencies.worker.CurrencyRefreshWorker

class CurrencyConverterApplication : Application() {

    lateinit var currencyConverterRepository: CurrencyConverterRepository

    override fun onCreate() {
        super.onCreate()
        initialize()
        scheduleWorker()
    }

    private fun scheduleWorker() {
        println("sujit app scheduleWorker")
        CurrencyRefreshWorker.scheduleWorker(this)
    }

    private fun initialize() {
        println("sujit app initialize")
        val coinExchangeRateService = RetrofitHelper.retrofitClient().create(
            CurrencyConverterService::class.java)
        val database = CurrencyConverterDatabase.getDatabase(applicationContext)
        currencyConverterRepository = CurrencyConverterRepository(coinExchangeRateService, database.getCurrencyConverterDao(), applicationContext)
    }
}