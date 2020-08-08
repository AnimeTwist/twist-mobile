package dev.smoketrees.twist.ui.home

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.smoketrees.twist.model.twist.*
import dev.smoketrees.twist.pagination.FilteredKitsuDataSourceFactory
import dev.smoketrees.twist.pagination.KitsuDataSourceFactory
import dev.smoketrees.twist.repository.AnimeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnimeViewModel(private val repo: AnimeRepo) : ViewModel() {
    private val _dbAnime = repo.getAllDbAnime()
    private val _dbTrendingAnime = repo.getDbTrendingAnime()
    val motdLiveData = repo.getMotd()
    val allAnimeLivedata = MediatorLiveData<Result<List<AnimeItem>>>()
    val trendingAnimeLiveData = MediatorLiveData<Result<List<TrendingAnimeItem>>>()

    fun getAllAnime() = viewModelScope.launch(Dispatchers.IO) {
        val result = repo.getAllNetworkAnime()
        when (result.status) {
            Result.Status.SUCCESS -> {
                result.data?.let { repo.saveAnime(it) }
                areAllLoaded.postValue(true)
                lastCode.postValue(null)
            }
            Result.Status.ERROR -> lastCode.postValue(result.message!!.code)
            else -> {
            }
        }
    }

    fun updateUserLibrary(jwt: String, body: PatchLibRequest) = repo.updateUserLibrary(jwt, body)

    fun getTrendingAnime(limit: Int) = viewModelScope.launch(Dispatchers.IO) {
        val result = repo.getNetworkTrendingAnime(limit)
        when (result.status) {
            Result.Status.SUCCESS -> {
                result.data?.let { repo.saveTrendingAnime(it) }
            }
            else -> {
            }
        }
    }

    val searchResults: MutableLiveData<List<AnimeItem>> = MutableLiveData()
    val searchQuery = MutableLiveData<String>()

    var areAllLoaded = MutableLiveData<Boolean>(false)
    var lastCode = MutableLiveData<Int>(null)

    var topAiringAnime: LiveData<PagedList<AnimeItem>>
    var topAiringAnimeNetworkState: LiveData<Result<List<AnimeItem>?>>
    var topRatedAnime: LiveData<PagedList<AnimeItem>>

    val userLibrary = MutableLiveData<Map<String, Map<String, LibraryEpisode>>>()

    fun signIn(loginDetails: LoginDetails) = repo.signIn(loginDetails)
    fun signUp(registerDetails: RegisterDetails) = repo.signUp(registerDetails)

    fun getAnimeByIds(ids: List<Int>) = repo.getAnimeByIds(ids)

    fun getUserLibrary(jwt: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = repo.getUserLibrarySync(jwt)

        when (result.status) {
            Result.Status.SUCCESS -> {
                userLibrary.postValue(result.data!!)
            }

            else -> {
            }
        }

    }

    init {
        allAnimeLivedata.addSource(_dbAnime) {
            allAnimeLivedata.value = if (it.isEmpty()) Result.loading() else Result.success(it)
        }

        trendingAnimeLiveData.addSource(_dbTrendingAnime) {
            trendingAnimeLiveData.value = if (it.isEmpty()) Result.loading() else Result.success(it)
        }

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