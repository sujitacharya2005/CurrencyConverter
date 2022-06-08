package com.android.currencies.repository

import android.content.Context
import com.android.currencies.data.local.CurrencyConverterDao
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.data.remote.CurrencyConverterService
import com.android.currencies.data.remote.apiCall
import com.android.currencies.data.remote.model.LatestCurrencyResponse

class CurrencyConverterRepository(private val currencyConverterService: CurrencyConverterService,
                                  private val currencyConverterDao: CurrencyConverterDao,
                                  private val applicationContext: Context) {

    suspend fun getRates() : List<CurrencyData> {
        return getLocalExchangeRates()
    }

    /*
    Fetch from room db
     */
    private fun getLocalExchangeRates(): List<CurrencyData> {
        return mutableListOf()
    }

    /*
   Fetch from remote api
    */
    suspend fun getRemoteExchangeRates() = apiCall {
        currencyConverterService.getLatest()
    }

    fun insertCurrenciesData(currencies: Map<String, Double>?) {
        if(currencies != null) {
            for((key, value) in currencies) {
                currencyConverterDao.insertCurrency(CurrencyData(symbol = key, value = value))
            }
        }
    }

    fun getSymbols() =
        currencyConverterDao.getCurrencies()


}