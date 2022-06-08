package com.android.currencies.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.android.currencies.data.local.model.CurrencyData
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * [fromCurrencyData] which is selected in spinner
 * [toCurrencyData] which is selected in Grid item
 * [enteredValue]  value is in double which entered in edit text
 * [return value] from currency to currency conversion will happen that value will update in edit text
 */
fun convertToCurrency(fromCurrencyData: CurrencyData?,
                      toCurrencyData: CurrencyData?,
                      enteredValue: Double?) : Double {
    val valueInDollars = convertFromCurrencyToDollar(enteredValue?.toBigDecimal()?: 0.0.toBigDecimal(), fromCurrencyData?.value?:0.0)
    return convertDollarToCurrency(valueInDollars, toCurrencyData?.value?:0.0).toDouble()
}

private fun convertFromCurrencyToDollar(amount: BigDecimal, fromRate: Double): BigDecimal {
    val scale = 50
    return amount.divide(BigDecimal.valueOf(fromRate), scale, RoundingMode.HALF_UP)
}

private fun convertDollarToCurrency(dollarValue: BigDecimal, toRate: Double): BigDecimal {
    return dollarValue.multiply(BigDecimal.valueOf(toRate))
}

fun availableInternet(context: Context): Boolean {
    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            ) ?: false
        } else {
            (@Suppress("DEPRECATION")
            return this.activeNetworkInfo?.isConnected ?: false)
        }
    }
}

