package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.builder.ToStringBuilder

@Keep
@Entity
class TrendingAnimeItem : AnimeItem() {
    companion object {
        var DIFF_CALLBACK: DiffUtil.ItemCallback<TrendingAnimeItem> =
            object : DiffUtil.ItemCallback<TrendingAnimeItem>() {
                override fun areItemsTheSame(oldItem: TrendingAnimeItem, newItem: TrendingAnimeItem): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: TrendingAnimeItem, newItem: TrendingAnimeItem): Boolean {
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