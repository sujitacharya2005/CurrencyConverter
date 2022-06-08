package com.android.currencies.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "ApiHelper"

/**
 * Wrap a suspending API [call] in try/catch. In case an exception is thrown, a [ApiResult.Error] is
 * created based on the exception type.
 * This also makes api call to be done on BACKGROUND thread.
 */
suspend fun <T : Any> apiCall(call: suspend () -> T): ApiResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            ApiResult.Success(call())
        } catch (e: Exception) {
            val error = when (e) {
                is HttpException -> {
                    val code = e.code()
                    ApiResult.Error(e, code)
                }
                is IOException -> ApiResult.NetworkError
                else -> {
                    ApiResult.Error(null)
                }
            }
            Log.d(TAG, "Error happened in making api call : $error ")
            error
        }
    }
}