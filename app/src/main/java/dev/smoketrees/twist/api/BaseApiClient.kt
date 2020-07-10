package dev.smoketrees.twist.api

import dev.smoketrees.twist.BuildConfig
import dev.smoketrees.twist.model.twist.Result
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

open class BaseApiClient {

    protected suspend fun <T> getResult(request: suspend () -> Response<T>): Result<T> {
        try {
            val response = request()
            return if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.error("Server response error")
                }
            } else {
                Result.error("${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: e.toString()
            return when (e) {
                is SocketTimeoutException -> {
                    Result.error("408 Timed out!")
                }
                is ConnectException -> {
                    Result.error("111 Check your internet connection!")
                }
                else -> {
                    Result.error(errorMessage)
                }
            }
        }
    }
}