package dev.smoketrees.twist.repository

import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.api.jikan.JikanWebClient
import dev.smoketrees.twist.db.AnimeDao
import dev.smoketrees.twist.db.AnimeDetailsDao
import dev.smoketrees.twist.model.jikan.JikanSearchModel
import dev.smoketrees.twist.model.twist.AnimeDetails
import dev.smoketrees.twist.model.twist.AnimeDetailsEntity
import dev.smoketrees.twist.model.twist.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimeRepo(
    private val webClient: AnimeWebClient,
    private val jikanClient: JikanWebClient,
    val animeDao: AnimeDao,
    val episodeDao: AnimeDetailsDao
) : BaseRepo() {
    fun getAllAnime() = makeRequestAndSave(
        databaseQuery = { animeDao.getAllAnime() },
        netWorkCall = { webClient.getAllAnime() },
        saveCallResult = {
            animeDao.saveAnime(it)
            fetchUrl()
        }
    )

    private suspend fun fetchUrl() = withContext(Dispatchers.IO) {
        //        val deferredList = animeList.map { animeItem ->
//            async {
//                val result = jikanClient.getAnimeByName(animeItem.slug?.slug ?: "")
//                if (result.status == Result.Status.SUCCESS) {
//                    result.data?.results?.get(0)?.let { jikanResult ->
//                        animeItem.imgUrl = jikanResult.imageUrl
//                        animeDao.saveAnime(animeItem)
//                    }
//                }
//            }
//        }
//        deferredList.awaitAll()

        animeDao.getAllAnimeList().forEach { animeItem ->
            if (animeItem.imgUrl.isNullOrBlank()) {
                val result = jikanClient.getAnimeByName(animeItem.slug?.slug ?: "")
                if (result.status == Result.Status.SUCCESS) {
                    if (result.data?.results?.isNotEmpty() == true) {
                        result.data.results[0].let { jikanResult ->
                            animeItem.imgUrl = jikanResult.imageUrl
                            animeDao.saveAnime(animeItem)
                        }
                    }
                }
                Thread.sleep(2000)
            }
        }
    }

    fun getAnimeDetails(name: String, id: Int) = makeRequestAndSave(
        databaseQuery = { episodeDao.getAnimeDetails(id) },
        netWorkCall = {
            saveEpisodeDetails(
                webClient.getAnimeDetails(name),
                jikanClient.getAnimeByName(name)
            )
        },
        saveCallResult = {
            episodeDao.saveAnimeDetails(it)
        }
    )

    private fun saveEpisodeDetails(
        episodeResult: Result<AnimeDetails>,
        detailsResult: Result<JikanSearchModel>
    ): Result<AnimeDetailsEntity> {
        return if (episodeResult.status == Result.Status.SUCCESS && detailsResult.status == Result.Status.SUCCESS) {
            Result.success(
                getAnimeDetailsEntity(
                    episodeResult.data!!,
                    if (detailsResult.data?.results.isNullOrEmpty())
                        null
                    else detailsResult.data?.results?.get(0)
                )
            )
        } else {
            Result.error("")
        }
    }

    private fun getAnimeDetailsEntity(
        episodeDetails: AnimeDetails,
        result: JikanSearchModel.Result?
    ) = AnimeDetailsEntity(
        airing = result?.airing,
        endDate = result?.endDate,
        episodes = result?.episodes,
        imageUrl = result?.imageUrl,
        id = episodeDetails.id,
        malId = result?.malId,
        members = result?.members,
        rated = result?.rated,
        score = result?.score,
        startDate = result?.startDate,
        synopsis = result?.synopsis,
        title = result?.title,
        type = result?.type,
        url = result?.url,
        episodeList = episodeDetails.episodes!!
    )


    fun getAnimeSources(animeName: String) = makeRequest {
        webClient.getAnimeSources(animeName)
    }

    fun getMALAnime(animeName: String) = makeRequest {
        jikanClient.getAnimeByName(animeName)
    }

    fun getMALAnimeById(id: Int) = makeRequest {
        jikanClient.getAnimeById(id)
    }

    fun searchAnime(animeName: String) = animeDao.searchAnime(animeName)

    fun getSeasonalAnime() = makeRequest {
        jikanClient.getSeasonalAnime()
    }
}