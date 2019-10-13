package dev.smoketrees.twist.api.anime

import dev.smoketrees.twist.api.BaseApiClient

class AnimeWebClient(private val webService: AnimeWebService): BaseApiClient() {
    suspend fun getAllAnime() = getResult {
        webService.getAllAnime()
    }

    suspend fun getAnimeDetails(animeName: String) = getResult {
        webService.getAnimeDetails(animeName)
    }

    suspend fun getAnimeSources(animeName: String) = getResult {
        webService.getAnimeSources(animeName)
    }
}