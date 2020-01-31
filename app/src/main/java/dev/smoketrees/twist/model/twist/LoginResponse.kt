package dev.smoketrees.twist.model.twist

data class LoginResponse(
    val donation_rank: Int,
    val id: Int,
    val rank: Int,
    val token: String,
    val username: String
)