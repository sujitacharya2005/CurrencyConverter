package com.android.currencies.data.remote.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatestCurrencyResponse(
    val timestamp: Int?,
    val base: String?,
    val rates: Map<String, Double>?
)