package dev.smoketrees.twist.api.jikan

import dev.smoketrees.twist.api.BaseApiClient

class MALWebClient(private val webService: MALWebService) : BaseApiClient() {
    suspend fun getAnimeById(animeId: Int) = getResult {
        webService.getAnimeById(animeId)
    }

    suspend fun getAnimeByName(animeName: String) = getResult {
        webService.getAnimeByName(animeName, 1)
    }

    suspend fun getSeasonalAnime() = getResult {
        webService.getSeasonalAnime("2019", "fall")
    }
}