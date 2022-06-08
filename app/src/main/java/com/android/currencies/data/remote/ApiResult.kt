package com.android.currencies.data.remote


private const val UNKNOWN_ERROR = 520

const val UNAUTHORIZED_ERROR = 401

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ApiResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception?,
                     val statusCode: Int? = UNKNOWN_ERROR
    ) : ApiResult<Nothing>()

    object NetworkError : ApiResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is NetworkError -> "No Network Error"
        }
    }
}
