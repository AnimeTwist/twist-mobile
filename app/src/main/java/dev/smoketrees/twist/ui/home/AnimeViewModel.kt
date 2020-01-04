package dev.smoketrees.twist.ui.home

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.smoketrees.twist.di.module.viewModelModule
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.pagination.KitsuDataSourceFactory
import dev.smoketrees.twist.pagination.PagedAnimeDatasource
import dev.smoketrees.twist.repository.AnimeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnimeViewModel(private val repo: AnimeRepo) : ViewModel() {
//    fun getAllAnime() = repo.getAllAnime()
    fun getOngoingAnime() = repo.getSeasonalAnime()

    fun searchAnime(animeName: String) = repo.searchAnime(animeName)
    fun getMALAnime(animeName: String) = repo.getMALAnime(animeName)
    fun getMALAnimeById(id: Int) = repo.getMALAnimeById(id)

    var animePagedList: LiveData<PagedList<AnimeItem>>
    private var liveDataSource: LiveData<PagedAnimeDatasource>

    init {
        val topAiringDataSourceFactory = KitsuDataSourceFactory(repo.webClient, "-user_count", "current")
        liveDataSource = topAiringDataSourceFactory.animeLiveDataSource

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        animePagedList = LivePagedListBuilder(topAiringDataSourceFactory, config).build()
    }

}