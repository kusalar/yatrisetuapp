package com.yatrisetu.app.util

sealed class Resource<out T> {
    data class Loading<out T>(val cachedData: T? = null) : Resource<T>()
    data class Success<out T>(val data: T, val isLive: Boolean, val cachedAt: Long? = null) : Resource<T>()
    data class Error<out T>(val message: String, val cachedData: T? = null, val cause: Throwable? = null) : Resource<T>()

    val dataOrNull: T?
        get() = when (this) {
            is Success -> data
            is Loading -> cachedData
            is Error -> cachedData
        }
}
