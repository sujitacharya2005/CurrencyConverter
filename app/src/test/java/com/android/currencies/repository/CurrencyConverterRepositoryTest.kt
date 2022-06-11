package com.android.currencies.repository

import com.android.currencies.data.local.CurrencyConverterDao
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.data.remote.ApiResult
import com.android.currencies.data.remote.CurrencyConverterService
import com.android.currencies.data.remote.model.LatestCurrencyResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class CurrencyConverterRepositoryTest {

    lateinit var currencyConverterRepository: CurrencyConverterRepository
    private val currencyConverterService: CurrencyConverterService = mockk()
    private val currencyConverterDao: CurrencyConverterDao = mockk()

    @Before
    fun setup() {
        currencyConverterRepository = CurrencyConverterRepository(currencyConverterService, currencyConverterDao)
    }



    @Test
    fun getRemoteExchangeRates() = runBlocking {
        val latestCurrencyResponse = LatestCurrencyResponse(1654974005, "USD", mapOf("AED" to 3.6731, "AFN" to 88.999995, "AMD" to 429.910968))
        coEvery { currencyConverterService.getLatest(any()) } returns latestCurrencyResponse
        val result: ApiResult<LatestCurrencyResponse> =  currencyConverterRepository.getRemoteExchangeRates()
        assertTrue(result is ApiResult.Success<LatestCurrencyResponse>)
        assertEquals((result as ApiResult.Success).data, latestCurrencyResponse)
    }

    @Test
    fun insertCurrenciesData() {
        val map = mapOf<String, Double>()
        every { currencyConverterDao.insertCurrenciesData(map) } returns Unit
        currencyConverterRepository.insertCurrenciesData(map)

        verify { currencyConverterDao.insertCurrenciesData(map) }

    }

    @Test
    fun getSymbols() {
        every { currencyConverterDao.getCurrencies() }  returns listOf(
                CurrencyData(id=1, symbol="AED", value=3.6731),
        CurrencyData(id=2, symbol="AFN", value=88.999995))
        val list = currencyConverterRepository.getSymbols()

        assertEquals(2, list.size)
        assertEquals("AED", list[0].symbol)
        assertEquals("AFN", list[1].symbol)
    }
}