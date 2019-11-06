package dev.smoketrees.twist.ui.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.smoketrees.twist.model.AnimeDetails
import dev.smoketrees.twist.model.Episode
import dev.smoketrees.twist.repository.AnimeRepo

class EpisodesViewModel(private val repo: AnimeRepo): ViewModel() {
    fun getAnimeDetails(animeName: String) = repo.getAnimeDetails(animeName)

    val episodeListLiveData: MutableLiveData<List<Episode>> = MutableLiveData()
}