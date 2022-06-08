package com.android.currencies.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.currencies.data.local.model.CurrencyData

@Database(entities = [CurrencyData::class], version = 1, exportSchema = false)
abstract class CurrencyConverterDatabase : RoomDatabase() {
    abstract fun getCurrencyConverterDao(): CurrencyConverterDao

    companion object {
        @Volatile
        private var INSTANCE: CurrencyConverterDatabase? = null

        private const val DB_NAME = "exchange_rates_database.db"

        fun getDatabase(context: Context): CurrencyConverterDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context.applicationContext,
                            CurrencyConverterDatabase::class.java, DB_NAME)
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}