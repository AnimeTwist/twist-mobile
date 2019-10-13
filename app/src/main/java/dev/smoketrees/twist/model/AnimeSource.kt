package dev.smoketrees.twist.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

class AnimeSource {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("source")
    @Expose
    var source: String? = null
    @SerializedName("number")
    @Expose
    var number: Int? = null
    @SerializedName("anime_id")
    @Expose
    var animeId: Int? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("source", source)
            .append("number", number).append("animeId", animeId).append("createdAt", createdAt)
            .append("updatedAt", updatedAt).toString()
    }

}