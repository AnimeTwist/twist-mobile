package dev.smoketrees.twist.model.twist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

class LoginDetails {

    @SerializedName("username")
    @Expose
    var username: String? = null
    @SerializedName("password")
    @Expose
    var password: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("username", username).append("password", password)
            .toString()
    }

}