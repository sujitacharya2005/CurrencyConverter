package com.android.currencies.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.android.currencies.constants.TAG_CHANGE
import com.android.currencies.data.local.model.CurrencyData
import com.android.currencies.repository.CurrencyConverterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val currencyConverterRepository: CurrencyConverterRepository,
    @ApplicationContext application: Context
) : ViewModel() {
    private val _symbolsLiveData = MutableLiveData<List<CurrencyData>>()
    val symbolsLiveData: LiveData<List<CurrencyData>> = _symbolsLiveData
    val updatesFromWorkerLiveData: LiveData<List<WorkInfo>> =
        WorkManager.getInstance(application)
            .getWorkInfosByTagLiveData(TAG_CHANGE)
    private val _progressBarLiveData = MutableLiveData(true)
    val progressBarLiveData: LiveData<Boolean> = _progressBarLiveData

    fun getCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            _symbolsLiveData.postValue(currencyConverterRepository.getSymbols())
            _progressBarLiveData.postValue(false)
        }
    }
}