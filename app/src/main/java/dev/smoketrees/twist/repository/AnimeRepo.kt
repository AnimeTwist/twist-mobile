package dev.smoketrees.twist.repository

import android.content.SharedPreferences
import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.api.jikan.JikanWebClient
import dev.smoketrees.twist.db.AnimeDao

class AnimeRepo(
    private val webClient: AnimeWebClient,
    private val jikanClient: JikanWebClient,
    val animeDao: AnimeDao
) : BaseRepo() {
    fun getAllAnime() = makeRequestAndSave(
        databaseQuery = { animeDao.getAllAnime() },
        netWorkCall = { webClient.getAllAnime() },
        saveCallResult = { animeDao.saveAnime(*it.toTypedArray()) }
    )

    fun getAnimeDetails(animeName: String) = makeRequest {
        webClient.getAnimeDetails(animeName)
    }

    fun getAnimeSources(animeName: String) = makeRequest {
        webClient.getAnimeSources(animeName)
    }

    fun getMALAnime(animeName: String) = makeRequest {
        jikanClient.getAnimeByName(animeName)
    }

    fun getMALAnimeById(id: Int) = makeRequest {
        jikanClient.getAnimeById(id)
    }

    fun searchAnime(animeName: String) = animeDao.searchAnime(animeName)

    fun getSeasonalAnime() = makeRequest {
        jikanClient.getSeasonalAnime()
    }
}