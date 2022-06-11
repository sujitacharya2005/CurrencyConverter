package com.android.currencies.prefs

import android.content.Context


private const val CURRENCY_PREFS = "auth_prefs"
private const val TIMESTAMP = "timestamp"
class CurrencyConverterPrefs(context: Context) {
    private val sharedPrefs = context.getSharedPreferences(CURRENCY_PREFS,
        Context.MODE_PRIVATE)
    private val editor = sharedPrefs.edit()

    var timeStamp:Long
        get() = sharedPrefs.getLong(TIMESTAMP, 0L)
        set(value) = editor.putLong(TIMESTAMP, value).apply()
}