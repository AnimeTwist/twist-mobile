package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

@Keep
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

    @Embedded
    @SerializedName("nejire_extension")
    @Expose
    var nejireExtension: NejireExtension? = null

    companion object {
        var DIFF_CALLBACK: DiffUtil.ItemCallback<AnimeItem> =
            object : DiffUtil.ItemCallback<AnimeItem>() {
                override fun areItemsTheSame(oldItem: AnimeItem, newItem: AnimeItem): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: AnimeItem, newItem: AnimeItem): Boolean {
                    return oldItem.id == newItem.id
                }
            }
    }


    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("title", title)
            .append("altTitle", altTitle).append("season", season).append("ongoing", ongoing)
            .append("hbId", hbId).append("hidden", hidden).append("malId", malId)
            .append("createdAt", createdAt).append("updatedAt", updatedAt).append("slug", slug)
            .append("nejireExtension", nejireExtension)
            .toString()
    }

}