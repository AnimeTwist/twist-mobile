package dev.smoketrees.twist.ui.home

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.LoginDetails
import dev.smoketrees.twist.model.twist.RegisterDetails
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.pagination.FilteredKitsuDataSourceFactory
import dev.smoketrees.twist.pagination.KitsuDataSourceFactory
import dev.smoketrees.twist.repository.AnimeRepo

class AnimeViewModel(private val repo: AnimeRepo) : ViewModel() {
    fun getAllAnime() = repo.getAllAnime()
    fun getMotd() = repo.getMotd()

    val searchResults: MutableLiveData<List<AnimeItem>> = MutableLiveData()
    val searchQuery = MutableLiveData<String>()

    var areAllLoaded = false

    var topAiringAnime: LiveData<PagedList<AnimeItem>>
    var topAiringAnimeNetworkState: LiveData<Result<List<AnimeItem>?>>
    var topRatedAnime: LiveData<PagedList<AnimeItem>>

    fun getTrendingAnime(limit: Int) = repo.getTrendingAnime(limit)

    fun signIn(loginDetails: LoginDetails) = repo.signIn(loginDetails)
    fun signUp(registerDetails: RegisterDetails) = repo.signUp(registerDetails)

    init {
        val topAiringDataSourceFactory =
            FilteredKitsuDataSourceFactory(repo.webClient, "-user_count", "current")
        topAiringAnimeNetworkState = topAiringDataSourceFactory.animeLiveDataSource.switchMap {
            it.animeLiveData
        }

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()
        topAiringAnime = LivePagedListBuilder(topAiringDataSourceFactory, config).build()

        val topRatedDataSourceFactory = KitsuDataSourceFactory(repo.webClient, "-average_rating")
        topRatedAnime = LivePagedListBuilder(topRatedDataSourceFactory, config).build()
    }
}