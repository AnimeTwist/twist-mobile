package dev.smoketrees.twist.ui.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.smoketrees.twist.model.twist.Episode
import dev.smoketrees.twist.repository.AnimeRepo

class EpisodesViewModel(private val repo: AnimeRepo) : ViewModel() {
    fun getAnimeDetails(animeName: String, id: Int) = repo.getAnimeDetails(animeName, id)

    val episodeListLiveData: MutableLiveData<List<Episode>> = MutableLiveData()
}