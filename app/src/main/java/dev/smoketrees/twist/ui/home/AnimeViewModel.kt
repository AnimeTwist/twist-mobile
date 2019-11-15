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

    fun getAnimeImageUrl(animeItem: AnimeItem) {
        repo.getMALAnime(animeItem.slug!!.slug!!).observe(lifeCycleOwner, Observer {
            animeItem.imgUrl = it.data?.results?.get(0)?.imageUrl
            viewModelScope.launch(Dispatchers.IO) {
                repo.animeDao.saveAnime(animeItem)
            }
        })
    }

    lateinit var lifeCycleOwner: LifecycleOwner
}