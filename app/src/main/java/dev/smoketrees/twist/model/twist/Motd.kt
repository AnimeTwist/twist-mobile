package dev.smoketrees.twist.model.twist

import java.io.Serializable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang3.builder.ToStringBuilder

class Motd : Serializable {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("title", title)
            .append("message", message).toString()
    }
}