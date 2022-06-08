package com.android.currencies.data.local

import androidx.room.*
import com.android.currencies.data.local.model.CurrencyData

@Dao
interface CurrencyConverterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrency(currency: CurrencyData)

    @Query("SELECT * FROM currency")
    fun getCurrencies(): List<CurrencyData>
}