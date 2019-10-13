package dev.smoketrees.twist.repository

import android.content.SharedPreferences
import androidx.lifecycle.liveData
import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.model.Result

class AnimeRepo(
    private val webClient: AnimeWebClient,
    private val prefs: SharedPreferences
) : BaseRepo() {
    fun getAllAnime() = liveData {
        emit(Result.loading())

        val response = webClient.getAllAnime()

        when (response.status) {
            Result.Status.SUCCESS -> {
                emit(Result.success(response.data))
            }
            Result.Status.ERROR -> {
                emit(Result.error(response.message!!))
            }
            else -> {
            }
        }
    }

    fun getAnimeDetails(animeName: String) = liveData {
        emit(Result.loading())

        val response = webClient.getAnimeDetails(animeName)

        when (response.status) {
            Result.Status.SUCCESS -> {
                emit(Result.success(response.data))
            }
            Result.Status.ERROR -> {
                emit(Result.error(response.message!!))
            }
            else -> {
            }
        }
    }

    fun getAnimeSources(animeName: String) = liveData {
        emit(Result.loading())

        val response = webClient.getAnimeSources(animeName)

        when (response.status) {
            Result.Status.SUCCESS -> {
                emit(Result.success(response.data))
            }
            Result.Status.ERROR -> {
                emit(Result.error(response.message!!))
            }
            else -> {
            }
        }
    }
}