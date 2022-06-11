package com.android.currencies.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.data.remote.ApiResult
import com.android.currencies.repository.CurrencyConverterRepository
import com.android.currencies.util.convertToCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Currency:ViewModel"
@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val currencyConverterRepository: CurrencyConverterRepository,
    @ApplicationContext application: Context
) : ViewModel() {
    private val _symbolsLiveData = MutableLiveData<List<CurrencyData>>()
    val symbolsLiveData: LiveData<List<CurrencyData>> = _symbolsLiveData

    private val _progressBarLiveData = MutableLiveData(true)
    val progressBarLiveData: LiveData<Boolean> = _progressBarLiveData

    private val _convertedValueLiveData = MutableLiveData(0.0)
    val convertedValueLiveData: LiveData<Double> = _convertedValueLiveData

    private val _convertedRatesSuccessLiveData = MutableLiveData(false)
    val convertedRatesSuccessLiveData: LiveData<Boolean> = _convertedRatesSuccessLiveData

    val showSnackBarLiveData = MutableLiveData("")

    fun getCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            _symbolsLiveData.postValue(currencyConverterRepository.getSymbols())
            _progressBarLiveData.postValue(false)
        }
    }

    fun getConversionRates() {
        viewModelScope.launch(Dispatchers.IO) {
            _progressBarLiveData.postValue(true)
            val repo = currencyConverterRepository

            when (val result = repo.getRemoteExchangeRates()) {
                is ApiResult.Success -> {
                    repo.insertCurrenciesData(result.data.rates)
                    _convertedRatesSuccessLiveData.postValue(true)
                    getCurrencies()
                }
                is ApiResult.NetworkError -> {
                    if(_symbolsLiveData.value.isNullOrEmpty()) {
                        Log.e(TAG, " No Internet")
                        showSnackBarLiveData.postValue("Check network connection and try again.")
                        _progressBarLiveData.postValue(false)
                    }
                }
                is ApiResult.Error ->
                    if(_symbolsLiveData.value.isNullOrEmpty()) {
                        showSnackBarLiveData.postValue("Something went wrong. Please try again.")
                        _progressBarLiveData.postValue(false)
                    }
            }
        }

    }



    fun getConvertedValue(fromCurrencyData:CurrencyData?,
                          toCurrencyData:CurrencyData?, enteredValue:Double) {
        val convertedValue = convertToCurrency(fromCurrencyData, toCurrencyData, enteredValue)
        _convertedValueLiveData.postValue(convertedValue)
    }

}