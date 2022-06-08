package com.android.currencies.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.android.currencies.CurrencyConverterApplication
import com.android.currencies.constants.TAG_CHANGE
import com.android.currencies.data.remote.ApiResult
import java.util.concurrent.TimeUnit

private const val TAG = "CurrencyRefreshWorker"

class CurrencyRefreshWorker(
    private val appContext: Context, workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repo = (appContext as CurrencyConverterApplication).currencyConverterRepository;

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

            val work = PeriodicWorkRequest.Builder(CurrencyRefreshWorker::class.java,
                30L,
                TimeUnit.MINUTES
            )
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    30,
                    TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag(TAG_CHANGE)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("currency_worker",
                    ExistingPeriodicWorkPolicy.KEEP, work)
        }
    }

}

