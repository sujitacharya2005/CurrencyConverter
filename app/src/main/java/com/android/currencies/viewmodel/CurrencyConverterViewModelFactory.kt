package com.android.currencies.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.currencies.repository.CurrencyConverterRepository

class CurrencyConverterViewModelFactory(
    private val repository: CurrencyConverterRepository,
    private val application: Application
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrencyConverterViewModel(repository, application) as T
    }
}