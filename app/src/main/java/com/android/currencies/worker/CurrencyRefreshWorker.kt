package com.android.currencies.worker

import android.content.Context
import androidx.work.*
import com.android.currencies.CurrencyConverterApplication
import com.android.currencies.data.remote.ApiResult
import java.util.concurrent.TimeUnit

class CurrencyRefreshWorker(
    private val appContext: Context, workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        println("sujit worker doWork")

        val repo = (appContext as CurrencyConverterApplication).currencyConverterRepository;

        when (val result = repo.getRemoteExchangeRates()) {
            is ApiResult.Success -> {
                println("sujit success " + result.data.rates)
                repo.insertCurrenciesData(result.data.rates)
                return Result.success()
            }
            is ApiResult.NetworkError -> {
                println("sujit No Internet")
                return Result.retry()
            }
            is ApiResult.Error -> println("sujit Error..." + result.statusCode)
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
                .build()
            println("sujit worker scheduleWorker")

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("currency_worker",
                    ExistingPeriodicWorkPolicy.KEEP, work)
        }
    }

}

