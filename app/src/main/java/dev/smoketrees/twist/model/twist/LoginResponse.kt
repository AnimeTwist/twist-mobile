package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep

@Keep
data class LoginResponse(
    val donation_rank: Int,
    val id: Int,
    val rank: Int,
    val token: String,
    val username: String
)