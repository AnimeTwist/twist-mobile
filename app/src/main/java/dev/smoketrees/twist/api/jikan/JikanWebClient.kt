package dev.smoketrees.twist.api.jikan

import dev.smoketrees.twist.api.BaseApiClient

class JikanWebClient(private val webService: JikanWebService) : BaseApiClient() {
    suspend fun getAnimeById(animeId: Int) = getResult {
        webService.getAnimeById(animeId)
    }

    suspend fun getAnimeByName(animeName: String) = getResult {
        webService.getAnimeByName(animeName, 1)
    }
}