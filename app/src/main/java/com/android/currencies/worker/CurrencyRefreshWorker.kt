package com.android.currencies.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.android.currencies.constants.TAG_CHANGE
import com.android.currencies.data.remote.ApiResult
import com.android.currencies.repository.CurrencyConverterRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

private const val TAG = "CurrencyRefreshWorker"

@HiltWorker
class CurrencyRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val currencyConverterRepository: CurrencyConverterRepository
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        val repo = currencyConverterRepository

        when (val result = repo.getRemoteExchangeRates()) {
            is ApiResult.Success -> {
                repo.insertCurrenciesData(result.data.rates)
                return Result.success()
            }
            is ApiResult.NetworkError -> {
                Log.e(TAG, "doWork: No Internet")
                return Result.retry()
            }
            is ApiResult.Error -> Log.e(TAG, "doWork:Error..." + result.statusCode)
        }

        return Result.failure()
    }

    companion object {
        fun scheduleWorker(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val work = PeriodicWorkRequest.Builder(CurrencyRefreshWorker::class.java, 30L, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag(TAG_CHANGE)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("currency_worker",
                    ExistingPeriodicWorkPolicy.KEEP, work)
        }
    }

}

