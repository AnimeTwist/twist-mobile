package dev.smoketrees.twist.pagination

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.model.twist.AnimeItem

class FilteredKitsuDataSourceFactory(
    private val webClient: AnimeWebClient,
    private val sort: String,
    private val filter: String
) : DataSource.Factory<Int, AnimeItem>() {
    val animeLiveDataSource = MutableLiveData<FilteredPagedAnimeDatasource>()

    override fun create(): DataSource<Int, AnimeItem> {
        val animeDataSource = FilteredPagedAnimeDatasource(webClient, sort, filter)
        animeLiveDataSource.postValue(animeDataSource)
        return animeDataSource
    }
}