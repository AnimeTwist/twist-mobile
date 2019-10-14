package dev.smoketrees.twist.repository

import androidx.lifecycle.liveData
import dev.smoketrees.twist.model.Result

open class BaseRepo {

    protected fun <T> makeRequest(request: suspend () -> Result<T>) = liveData {
        emit(Result.loading())

        val response = request.invoke()

        when (response.status) {
            Result.Status.SUCCESS -> {
                emit(Result.success(response.data))
            }
            Result.Status.ERROR -> {
                emit(Result.error(response.message!!))
            }
            else -> {
            }
        }
    }
}