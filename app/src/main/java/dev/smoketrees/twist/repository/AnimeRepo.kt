package dev.smoketrees.twist.repository

import android.content.SharedPreferences
import dev.smoketrees.twist.api.anime.AnimeWebClient

class AnimeRepo(
    private val webClient: AnimeWebClient,
    private val prefs: SharedPreferences
) : BaseRepo() {
    fun getAllAnime() = makeRequest {
        webClient.getAllAnime()
    }

    fun getAnimeDetails(animeName: String) = makeRequest {
        webClient.getAnimeDetails(animeName)
    }

    fun getAnimeSources(animeName: String) = makeRequest {
        webClient.getAnimeSources(animeName)
    }
}