package dev.smoketrees.twist.model.jikan
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SeasonalAnime(
    @SerializedName("anime")
    val anime: List<Anime> = listOf(),
    @SerializedName("request_cache_expiry")
    val requestCacheExpiry: Int = 0,
    @SerializedName("request_cached")
    val requestCached: Boolean = false,
    @SerializedName("request_hash")
    val requestHash: String = "",
    @SerializedName("season_name")
    val seasonName: String = "",
    @SerializedName("season_year")
    val seasonYear: Int = 0
) {
    data class Anime(
        @SerializedName("airing_start")
        val airingStart: String? = "",
        @SerializedName("continuing")
        val continuing: Boolean = false,
        @SerializedName("episodes")
        val episodes: Int? = 0,
        @SerializedName("genres")
        val genres: List<Genre> = listOf(),
        @SerializedName("image_url")
        val imageUrl: String = "",
        @SerializedName("kids")
        val kids: Boolean = false,
        @SerializedName("licensors")
        val licensors: List<Any> = listOf(),
        @SerializedName("mal_id")
        val malId: Int = 0,
        @SerializedName("members")
        val members: Int = 0,
        @SerializedName("producers")
        val producers: List<Any> = listOf(),
        @SerializedName("r18")
        val r18: Boolean = false,
        @SerializedName("score")
        val score: Double? = 0.0,
        @SerializedName("source")
        val source: String = "",
        @SerializedName("synopsis")
        val synopsis: String = "",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("type")
        val type: String = "",
        @SerializedName("url")
        val url: String = ""
    ) {
        data class Genre(
            @SerializedName("mal_id")
            val malId: Int = 0,
            @SerializedName("name")
            val name: String = "",
            @SerializedName("type")
            val type: String = "",
            @SerializedName("url")
            val url: String = ""
        )
    }
}