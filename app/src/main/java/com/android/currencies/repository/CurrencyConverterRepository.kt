package com.android.currencies.repository

import com.android.currencies.data.local.CurrencyConverterDao
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.data.remote.CurrencyConverterService
import com.android.currencies.data.remote.apiCall
import javax.inject.Inject


class CurrencyConverterRepository  @Inject constructor(private val currencyConverterService: CurrencyConverterService,
                                                       private val currencyConverterDao: CurrencyConverterDao) {

    /**
     * This is to get exchange rates from remote api
     */
    suspend fun getRemoteExchangeRates() = apiCall {
        currencyConverterService.getLatest()
    }

    /**
     * What ever data received from remote api save to local database
     */
    fun insertCurrenciesData(currencies: Map<String, Double>?) {
        currencyConverterDao.insertCurrenciesData(currencies)
    }

    /**
     * Get all symbols and value from DB
     */
    fun getSymbols() =
        currencyConverterDao.getCurrencies()


}