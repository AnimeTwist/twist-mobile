package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class AnimeDetailsEntity(
    val airing: Boolean? = false,
    val endDate: String? = "",
    val episodes: Int? = 0,
    val imageUrl: String? = "",
    @PrimaryKey
    @ColumnInfo(name = "anime_id")
    val id: Int? = 0,
    val malId: Int? = 0,
    val members: Int? = 0,
    val rated: String? = "",
    val score: Double? = 0.0,
    val startDate: String? = "",
    val synopsis: String? = "",
    val title: String? = "",
    val type: String? = "",
    val url: String? = "",
//    @TypeConverters(EpisodeListTypeConverter::class)
    val episodeList: List<Episode>
)