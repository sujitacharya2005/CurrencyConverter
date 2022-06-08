package com.android.currencies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.repository.CurrencyConverterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrencyConverterViewModel(private val currencyConverterRepository: CurrencyConverterRepository) : ViewModel() {
    private val _symbolsLiveData = MutableLiveData<List<CurrencyData>>()
    val symbolsLiveData:LiveData<List<CurrencyData>> = _symbolsLiveData

    fun getCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            _symbolsLiveData.postValue(currencyConverterRepository.getSymbols())
        }
    }
}