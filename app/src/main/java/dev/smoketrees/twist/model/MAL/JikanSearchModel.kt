package dev.smoketrees.twist.model.MAL
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class JikanSearchModel(
    @SerializedName("last_page")
    val lastPage: Int = 0,
    @SerializedName("request_cache_expiry")
    val requestCacheExpiry: Int = 0,
    @SerializedName("request_cached")
    val requestCached: Boolean = false,
    @SerializedName("request_hash")
    val requestHash: String = "",
    @SerializedName("results")
    val results: List<Result> = listOf()
) {
    data class Result(
        @SerializedName("airing")
        val airing: Boolean = false,
        @SerializedName("end_date")
        val endDate: String = "",
        @SerializedName("episodes")
        val episodes: Int = 0,
        @SerializedName("image_url")
        val imageUrl: String = "",
        @SerializedName("mal_id")
        val malId: Int = 0,
        @SerializedName("members")
        val members: Int = 0,
        @SerializedName("rated")
        val rated: String = "",
        @SerializedName("score")
        val score: Double = 0.0,
        @SerializedName("start_date")
        val startDate: String = "",
        @SerializedName("synopsis")
        val synopsis: String = "",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("type")
        val type: String = "",
        @SerializedName("url")
        val url: String = ""
    )
}