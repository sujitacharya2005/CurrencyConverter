package com.android.currencies.data.local

import androidx.room.*
import com.android.currencies.data.local.model.CurrencyData

@Dao
interface CurrencyConverterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrency(currency: CurrencyData)

    @Transaction
    fun insertCurrenciesData(currencies: Map<String, Double>?) {
        if(currencies != null) {
            for((key, value) in currencies) {
                insertCurrency(CurrencyData(symbol = key, value = value))
            }
        }
    }

    @Query("SELECT * FROM currency")
    fun getCurrencies(): List<CurrencyData>
}