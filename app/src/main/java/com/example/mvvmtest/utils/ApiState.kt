package com.example.mvvmtest.utils

sealed interface ApiState<out T> {

    /**
     * success response with body
     */
    data class Success<T>(val data: T) : ApiState<T>

    /**
     * Failure response
     */
    data class Error(val message: String) : ApiState<Nothing>

    /**
     * Loading
     */
    object Loading : ApiState<Nothing>

}