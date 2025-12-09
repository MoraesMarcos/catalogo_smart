package com.example.myapplication.domain.util

sealed class RemoteResult<out T> {
    data class Success<T>(val data: T) : RemoteResult<T>()
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : RemoteResult<Nothing>()
    object Loading : RemoteResult<Nothing>()
}