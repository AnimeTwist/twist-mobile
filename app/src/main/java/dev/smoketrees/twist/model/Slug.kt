package dev.smoketrees.twist.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

class Slug {

    @SerializedName("anime_id")
    @Expose
    var animeId: Int? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("slug")
    @Expose
    var slug: String? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("animeId", animeId).append("createdAt", createdAt)
            .append("id", id).append("slug", slug).append("updatedAt", updatedAt).toString()
    }

}