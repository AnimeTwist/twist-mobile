package dev.smoketrees.twist.model.jikan
import com.google.gson.annotations.SerializedName


data class MALAnime(
    @SerializedName("aired")
    val aired: Aired = Aired(),
    @SerializedName("airing")
    val airing: Boolean = false,
    @SerializedName("background")
    val background: Any? = Any(),
    @SerializedName("broadcast")
    val broadcast: Any? = Any(),
    @SerializedName("duration")
    val duration: String = "",
    @SerializedName("ending_themes")
    val endingThemes: List<String> = listOf(),
    @SerializedName("episodes")
    val episodes: Int = 0,
    @SerializedName("favorites")
    val favorites: Int = 0,
    @SerializedName("genres")
    val genres: List<Genre> = listOf(),
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("licensors")
    val licensors: List<Licensor> = listOf(),
    @SerializedName("mal_id")
    val malId: Int = 0,
    @SerializedName("members")
    val members: Int = 0,
    @SerializedName("opening_themes")
    val openingThemes: List<Any> = listOf(),
    @SerializedName("popularity")
    val popularity: Int = 0,
    @SerializedName("premiered")
    val premiered: Any? = Any(),
    @SerializedName("producers")
    val producers: List<Producer> = listOf(),
    @SerializedName("rank")
    val rank: Int = 0,
    @SerializedName("rating")
    val rating: String = "",
    @SerializedName("related")
    val related: Related = Related(),
    @SerializedName("request_cache_expiry")
    val requestCacheExpiry: Int = 0,
    @SerializedName("request_cached")
    val requestCached: Boolean = false,
    @SerializedName("request_hash")
    val requestHash: String = "",
    @SerializedName("score")
    val score: Double = 0.0,
    @SerializedName("scored_by")
    val scoredBy: Int = 0,
    @SerializedName("source")
    val source: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("studios")
    val studios: List<Studio> = listOf(),
    @SerializedName("synopsis")
    val synopsis: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("title_english")
    val titleEnglish: String = "",
    @SerializedName("title_japanese")
    val titleJapanese: String = "",
    @SerializedName("title_synonyms")
    val titleSynonyms: List<Any> = listOf(),
    @SerializedName("trailer_url")
    val trailerUrl: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("url")
    val url: String = ""
) {
    data class Aired(
        @SerializedName("from")
        val from: String = "",
        @SerializedName("prop")
        val prop: Prop = Prop(),
        @SerializedName("string")
        val string: String = "",
        @SerializedName("to")
        val to: Any? = Any()
    ) {
        data class Prop(
            @SerializedName("from")
            val from: From = From(),
            @SerializedName("to")
            val to: To = To()
        ) {
            data class From(
                @SerializedName("day")
                val day: Any? = Any(),
                @SerializedName("month")
                val month: Any? = Any(),
                @SerializedName("year")
                val year: Any? = Any()
            )

            data class To(
                @SerializedName("day")
                val day: Any? = Any(),
                @SerializedName("month")
                val month: Any? = Any(),
                @SerializedName("year")
                val year: Any? = Any()
            )
        }
    }

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

    data class Licensor(
        @SerializedName("mal_id")
        val malId: Int = 0,
        @SerializedName("name")
        val name: String = "",
        @SerializedName("type")
        val type: String = "",
        @SerializedName("url")
        val url: String = ""
    )

    data class Producer(
        @SerializedName("mal_id")
        val malId: Int = 0,
        @SerializedName("name")
        val name: String = "",
        @SerializedName("type")
        val type: String = "",
        @SerializedName("url")
        val url: String = ""
    )

    data class Related(
        @SerializedName("Adaptation")
        val adaptation: List<Adaptation> = listOf(),
        @SerializedName("Alternative version")
        val alternativeVersion: List<AlternativeVersion> = listOf()
    ) {
        data class Adaptation(
            @SerializedName("mal_id")
            val malId: Int = 0,
            @SerializedName("name")
            val name: String = "",
            @SerializedName("type")
            val type: String = "",
            @SerializedName("url")
            val url: String = ""
        )

        data class AlternativeVersion(
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

    data class Studio(
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