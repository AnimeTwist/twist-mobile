package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

@Keep
class RegisterDetails {

    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("password")
    @Expose
    var password: String? = null
    @SerializedName("password_confirm")
    @Expose
    var passwordConfirm: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("username", username).append("email", email)
            .append("password", password).append("passwordConfirm", passwordConfirm).toString()
    }

}