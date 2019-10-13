package dev.smoketrees.twist.ui

import androidx.lifecycle.ViewModel
import dev.smoketrees.twist.repository.AnimeRepo

class AnimeViewModel(private val repo: AnimeRepo) : ViewModel() {
    fun getAllAnime() = repo.getAllAnime()

    fun getAnimeDetails(animeName: String) = repo.getAnimeDetails(animeName)

    fun getAnimeSources(animeName: String) = repo.getAnimeSources(animeName)
}