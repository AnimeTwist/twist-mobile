package dev.smoketrees.twist.api

import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.Messages
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseApiClient {

    protected suspend fun <T> getResult(request: suspend () -> Response<T>): Result<T> {
        try {
            val response = request()
            return if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.error(Messages.Message(0,"Server response error"))
                }
            } else {
                Result.error(Messages.Message(response.code(), response.message()))
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: e.toString()
            return when (e) {
                is SocketTimeoutException -> {
                    Result.error(Messages.Message(408,"Timed out!"))
                }
                is ConnectException -> {
                    Result.error(Messages.Message(111,"Check your internet connection!"))
                }
                is UnknownHostException -> {
                    Result.error(Messages.Message(111,"Check your internet connection!"))
                }
                else -> {
                    Result.error(Messages.Message(0, errorMessage))
                }
            }
        }
    }
}