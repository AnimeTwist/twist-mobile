package dev.smoketrees.twist.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import dev.smoketrees.twist.model.twist.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    protected fun <T, A> makeRequestAndSave(
        databaseQuery: () -> LiveData<T>,
        netWorkCall: suspend () -> Result<A>,
        saveCallResult: suspend (A) -> Unit
    ): LiveData<Result<T>> = liveData(Dispatchers.IO) {
        emit(Result.loading())

        val source = databaseQuery.invoke().map { Result.success(it) }
        emitSource(source)

        val response = netWorkCall.invoke()
        when (response.status) {
            Result.Status.SUCCESS -> {
                saveCallResult(response.data!!)
            }
            Result.Status.ERROR -> {
                emit(Result.error(response.message!!))
                emitSource(source)
            }
            else -> {}
        }
    }
}