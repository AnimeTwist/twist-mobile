package dev.smoketrees.twist.model.twist

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import dev.smoketrees.twist.api.anime.AnimeWebClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PagedAnimeDatasource(val webClient: AnimeWebClient, val sort: String) :
    PageKeyedDataSource<Int, AnimeItem>() {
    val animeLiveData: MutableLiveData<Result<List<AnimeItem>?>> = MutableLiveData()


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, AnimeItem>
    ) {
        GlobalScope.launch {
            animeLiveData.postValue(Result.loading())
            val response = webClient.kitsuRequest(params.requestedLoadSize, sort, 0)
            when (response.status) {
                Result.Status.SUCCESS -> {
                    animeLiveData.postValue(Result.success(response.data))
                    response.data?.toMutableList()?.let { callback.onResult(it, null, 1) }
                }
                Result.Status.ERROR -> {
                    animeLiveData.postValue(Result.error(response.message!!))
                }
                else -> {
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, AnimeItem>) {
        GlobalScope.launch {
            animeLiveData.postValue(Result.loading())
            val response =
                webClient.kitsuRequest(params.requestedLoadSize, sort, params.key)
            when (response.status) {
                Result.Status.SUCCESS -> {
                    animeLiveData.postValue(Result.success(response.data))
                    response.data?.toMutableList()?.let { callback.onResult(it, params.key + 1) }
                }
                Result.Status.ERROR -> {
                    animeLiveData.postValue(Result.error(response.message!!))
                }
                else -> {
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, AnimeItem>) {
        GlobalScope.launch {
            animeLiveData.postValue(Result.loading())
            val response =
                webClient.kitsuRequest(params.requestedLoadSize, sort, params.key)
            when (response.status) {
                Result.Status.SUCCESS -> {
                    animeLiveData.postValue(Result.success(response.data))
                    val key = if (params.key > 1) params.key - 1 else 0
                    response.data?.toMutableList()?.let { callback.onResult(it, key) }
                }
                Result.Status.ERROR -> {
                    animeLiveData.postValue(Result.error(response.message!!))
                }
                else -> {
                }
            }
        }
    }
}

