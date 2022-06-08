package com.android.currencies.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val symbol: String,
    val value: Double,
)

