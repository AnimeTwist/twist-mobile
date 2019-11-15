package dev.smoketrees.twist.api.anime

import dev.smoketrees.twist.model.twist.AnimeDetails
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.AnimeSource
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AnimeWebService {
    @GET("anime")
    suspend fun getAllAnime(): Response<List<AnimeItem>>

    @GET("anime/{animeName}")
    suspend fun getAnimeDetails(@Path("animeName") animeName: String): Response<AnimeDetails>

    @GET("anime/{animeName}/sources")
    suspend fun getAnimeSources(@Path("animeName") animeName: String): Response<List<AnimeSource>>
}