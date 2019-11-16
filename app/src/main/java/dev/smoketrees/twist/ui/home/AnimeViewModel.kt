package dev.smoketrees.twist.ui.home

import androidx.lifecycle.*
import dev.smoketrees.twist.di.module.viewModelModule
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.repository.AnimeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnimeViewModel(private val repo: AnimeRepo) : ViewModel() {
    fun getAllAnime() = repo.getAllAnime()

    fun searchAnime(animeName: String) = repo.searchAnime(animeName)
    fun getMALAnime(animeName: String) = repo.getMALAnime(animeName)
    fun getMALAnimeById(id: Int) = repo.getMALAnimeById(id)

    lateinit var lifeCycleOwner: LifecycleOwner
}