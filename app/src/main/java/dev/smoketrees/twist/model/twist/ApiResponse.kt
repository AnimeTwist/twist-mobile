package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep

@Keep
data class ApiResponse(
    val status: String?,
    val message: String?,
    val token: String?
)