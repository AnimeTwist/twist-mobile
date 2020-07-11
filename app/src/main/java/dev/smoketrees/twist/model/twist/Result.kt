package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep
import dev.smoketrees.twist.utils.Messages

@Keep
data class Result<out T>(val status: Status, val data: T?, val message: Messages.Message?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Result<T> {
            return Result(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(message: Messages.Message, data: T? = null): Result<T> {
            return Result(
                Status.ERROR,
                data,
                message
            )
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(
                Status.LOADING,
                data,
                null
            )
        }
    }
}