package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep

@Keep
data class RegisterDetails(
    var username: String,
    var email: String,
    var password: String,
    var passwordConfirm: String
)