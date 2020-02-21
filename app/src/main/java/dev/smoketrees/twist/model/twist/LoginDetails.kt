package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep

@Keep
data class LoginDetails(
    var username: String,
    var password: String
)