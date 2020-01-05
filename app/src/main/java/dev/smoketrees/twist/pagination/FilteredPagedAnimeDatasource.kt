package dev.smoketrees.twist.pagination

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FilteredPagedAnimeDatasource(
    private val webClient: AnimeWebClient,
    private val sort: String,
    private val filter: String
) :
    PageKeyedDataSource<Int, AnimeItem>() {
    val animeLiveData: MutableLiveData<Result<List<AnimeItem>?>> = MutableLiveData()


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, AnimeItem>
    ) {
        GlobalScope.launch {
            animeLiveData.postValue(Result.loading())
            val response = webClient.filteredKitsuRequest(params.requestedLoadSize, sort, filter, 0)
            when (response.status) {
                Result.Status.SUCCESS -> {
                    animeLiveData.postValue(
                        Result.success(
                            response.data
                        )
                    )
                    response.data?.toMutableList()
                        ?.let { callback.onResult(it, null, params.requestedLoadSize) }
                }
                Result.Status.ERROR -> {
                    animeLiveData.postValue(
                        Result.error(
                            response.message!!
                        )
                    )
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
                webClient.filteredKitsuRequest(params.requestedLoadSize, sort, filter, params.key)
            when (response.status) {
                Result.Status.SUCCESS -> {
                    animeLiveData.postValue(
                        Result.success(
                            response.data
                        )
                    )
                    response.data?.toMutableList()
                        ?.let { callback.onResult(it, params.key + params.requestedLoadSize) }
                }
                Result.Status.ERROR -> {
                    animeLiveData.postValue(
                        Result.error(
                            response.message!!
                        )
                    )
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
                webClient.filteredKitsuRequest(params.requestedLoadSize, sort, filter, params.key)
            when (response.status) {
                Result.Status.SUCCESS -> {
                    animeLiveData.postValue(
                        Result.success(
                            response.data
                        )
                    )
                    val key = if (params.key > 1) params.key - params.requestedLoadSize else 0
                    response.data?.toMutableList()?.let { callback.onResult(it, key) }
                }
                Result.Status.ERROR -> {
                    animeLiveData.postValue(
                        Result.error(
                            response.message!!
                        )
                    )
                }
                else -> {
                }
            }
        }
    }
}

