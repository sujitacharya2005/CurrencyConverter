package com.android.currencies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.currencies.repository.CurrencyConverterRepository

class CurrencyConverterViewModelFactory(
    private val repository: CurrencyConverterRepository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrencyConverterViewModel(repository) as T
    }
}