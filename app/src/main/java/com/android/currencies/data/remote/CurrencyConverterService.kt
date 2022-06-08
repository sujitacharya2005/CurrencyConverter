package com.android.currencies.data.remote

import com.android.currencies.data.remote.model.LatestCurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val APP_ID = "f68dabcb49064ce6b2683ae184064af0"

interface CurrencyConverterService {
    @GET("latest.json")
    suspend fun getLatest(
        @Query("app_id")
        appId:String = APP_ID
    ) : LatestCurrencyResponse
}