package dev.smoketrees.twist.api.anime

import dev.smoketrees.twist.model.twist.*
import retrofit2.Response
import retrofit2.http.*

interface AnimeWebService {
    @GET("anime")
    suspend fun getAllAnime(): Response<List<AnimeItem>>

    @GET("anime/{animeName}")
    suspend fun getAnimeDetails(@Path("animeName") animeName: String): Response<AnimeDetails>

    @GET("anime/{animeName}/sources")
    suspend fun getAnimeSources(@Path("animeName") animeName: String): Response<List<AnimeSource>>

    @GET("list/anime")
    suspend fun filteredKitsuRequest(
        @Query("page[limit]") pageLimit: Int,
        @Query("sort") sort: String,
        @Query("filter[status]") filterStatus: String,
        @Query("page[offset]") pageOffset: Int
    ): Response<List<AnimeItem>>

    @GET("list/anime")
    suspend fun kitsuRequest(
        @Query("page[limit]") pageLimit: Int,
        @Query("sort") sort: String,
        @Query("page[offset]") pageOffset: Int
    ): Response<List<AnimeItem>>

    @GET("list/trending/anime")
    suspend fun getTrendingAnime(
        @Query("limit") limit: Int
    ): Response<List<AnimeItem>>

    @GET("motd")
    suspend fun getMotd(): Response<Motd>

    @POST("auth/signin")
    suspend fun signIn(@Body loginDetails: LoginDetails): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signUp(@Body registerDetails: RegisterDetails): Response<LoginResponse>
}