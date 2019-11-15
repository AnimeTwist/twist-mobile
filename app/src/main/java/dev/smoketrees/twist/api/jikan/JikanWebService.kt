package dev.smoketrees.twist.api.jikan

import dev.smoketrees.twist.model.jikan.JikanSearchModel
import dev.smoketrees.twist.model.jikan.MALAnime
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanWebService {
    @GET("anime/{animeId}")
    suspend fun getAnimeById(@Path("animeId") animeId: Int): Response<MALAnime>

    @GET("search/anime")
    suspend fun getAnimeByName(
        @Query("q") animeName: String,
        @Query("limit") limit: Int
    ): Response<JikanSearchModel>
}