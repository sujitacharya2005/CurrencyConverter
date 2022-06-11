package com.android.currencies.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.data.remote.ApiResult
import com.android.currencies.data.remote.model.LatestCurrencyResponse
import com.android.currencies.repository.CurrencyConverterRepository
import com.android.currencies.util.convertToCurrency
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CurrencyConverterViewModelTest {

    lateinit var currencyConverterViewModel:CurrencyConverterViewModel

    private val currencyConverterRepository: CurrencyConverterRepository = mockk()
    private val application: Context = mockk()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        currencyConverterViewModel = CurrencyConverterViewModel(currencyConverterRepository, application)
        mockIODispatcher()
    }

    @Test
    fun getCurrencies_emptyData() {
        every { currencyConverterRepository.getSymbols() } returns listOf()
        currencyConverterViewModel.getCurrencies()
        assertEquals(0, currencyConverterViewModel.symbolsLiveData.value!!.size)
    }

    @Test
    fun getCurrencies_someData() {
        every { currencyConverterRepository.getSymbols() } returns listOf(
            CurrencyData(id=1, symbol="AED", value=3.6731),
            CurrencyData(id=2, symbol="AFN", value=88.999995)
        )
        currencyConverterViewModel.getCurrencies()
        assertEquals(2, currencyConverterViewModel.symbolsLiveData.value!!.size)
    }

    @Test
    fun getConversionRates() {
        every { currencyConverterRepository.getSymbols() } returns listOf(
            CurrencyData(id=1, symbol="AED", value=3.6731),
            CurrencyData(id=2, symbol="AFN", value=88.999995),
            CurrencyData(id=2, symbol="AMD", value=429.910968)
        )
        val latestCurrencyResponse = LatestCurrencyResponse(1654974005, "USD", mapOf("AED" to 3.6731, "AFN" to 88.999995, "AMD" to 429.910968))
        coEvery { currencyConverterRepository.getRemoteExchangeRates() } returns ApiResult.Success(latestCurrencyResponse)
        every { currencyConverterRepository.insertCurrenciesData(latestCurrencyResponse.rates) } returns Unit
        currencyConverterViewModel.getConversionRates()
        assertEquals(3, currencyConverterViewModel.symbolsLiveData.value!!.size)
        assertEquals(3, currencyConverterViewModel.symbolsLiveData.value!!.size)
    }

    @Test
    fun getConvertedValue() {
        mockkStatic("com.android.currencies.util.CurrencyUtil")
        val from = CurrencyData(id=1, symbol="AED", value=3.6731)
        val to = CurrencyData(id=5, symbol="ANG", value=1.803037)
        val entered = 20.0
        every { convertToCurrency(from, to, entered)  } returns 9.817521984155073
        currencyConverterViewModel.getConvertedValue(from, to, entered)
        assertEquals(currencyConverterViewModel.convertedValueLiveData.value, 9.817521984155073)
    }

    private fun mockIODispatcher() {
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns dispatcher
    }
}