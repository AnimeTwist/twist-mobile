package dev.smoketrees.twist.model.twist

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

@Entity
class AnimeItem {

    @PrimaryKey
    @ColumnInfo(name = "uid")
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("alt_title")
    @Expose
    var altTitle: String? = null
    @SerializedName("season")
    @Expose
    var season: Int? = null
    @SerializedName("ongoing")
    @Expose
    var ongoing: Int? = null
    @SerializedName("hb_id")
    @Expose
    var hbId: Int? = null
    @Ignore
    @SerializedName("hidden")
    @Expose
    var hidden: Int? = null
    @SerializedName("mal_id")
    @Expose
    var malId: Int? = null
    @Ignore
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @Ignore
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
    @Embedded
    @SerializedName("slug")
    @Expose
    var slug: Slug? = null
    @ColumnInfo(name = "img_url")
    var imgUrl: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("title", title)
            .append("altTitle", altTitle).append("season", season).append("ongoing", ongoing)
            .append("hbId", hbId).append("hidden", hidden).append("malId", malId)
            .append("createdAt", createdAt).append("updatedAt", updatedAt).append("slug", slug)
            .toString()
    }

}