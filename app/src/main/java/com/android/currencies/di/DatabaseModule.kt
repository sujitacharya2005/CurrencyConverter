package com.android.currencies.di

import android.content.Context
import com.android.currencies.data.local.CurrencyConverterDao
import com.android.currencies.data.local.CurrencyConverterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideCurrencyDatabase(@ApplicationContext context: Context): CurrencyConverterDatabase {
        return CurrencyConverterDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideCurrencyDao(database: CurrencyConverterDatabase): CurrencyConverterDao {
        return database.getCurrencyConverterDao()
    }
}