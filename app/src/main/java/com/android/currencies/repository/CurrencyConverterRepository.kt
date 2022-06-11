package com.android.currencies.repository

import com.android.currencies.data.local.CurrencyConverterDao
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.data.remote.CurrencyConverterService
import com.android.currencies.data.remote.apiCall
import javax.inject.Inject

class CurrencyConverterRepository  @Inject constructor(private val currencyConverterService: CurrencyConverterService,
                                                       private val currencyConverterDao: CurrencyConverterDao) {

    /*
   Fetch from remote api
    */
    suspend fun getRemoteExchangeRates() = apiCall {
        currencyConverterService.getLatest()
    }

    fun insertCurrenciesData(currencies: Map<String, Double>?) {
        currencyConverterDao.insertCurrenciesData(currencies)
    }

    fun getSymbols() =
        currencyConverterDao.getCurrencies()


}