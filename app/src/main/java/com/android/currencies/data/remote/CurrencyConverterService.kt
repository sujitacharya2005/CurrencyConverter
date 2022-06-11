package com.android.currencies.data.remote

import com.android.currencies.data.remote.model.LatestCurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val APP_ID = "9bb48e983bb94df39d3f7c5b04745b13"

interface CurrencyConverterService {

    @GET("latest.json")
    suspend fun getLatest(
        @Query("app_id")
        appId:String = APP_ID
    ) : LatestCurrencyResponse

}