package com.android.currencies

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.android.currencies.worker.CurrencyRefreshWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CurrencyConverterApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    companion object {
        private var isWorkerScheduled = false
    }

    override fun onCreate() {
        super.onCreate()
        if(!isWorkerScheduled) {
            scheduleWorker(this)
            isWorkerScheduled = true
        }
    }

    private fun scheduleWorker(context: Context) {
        applicationScope.launch {
            CurrencyRefreshWorker.scheduleWorker(context)
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    }
}