package dev.smoketrees.twist.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.repository.AnimeRepo

class AnimeViewModel(private val repo: AnimeRepo) : ViewModel() {
    fun getAllAnime() = repo.getAllAnime()

    val animeListLiveData: MutableLiveData<List<AnimeItem>> = MutableLiveData()
}