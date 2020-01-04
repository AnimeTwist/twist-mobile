package dev.smoketrees.twist.repository


import android.util.Log
import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.api.jikan.MALWebClient
import dev.smoketrees.twist.db.AnimeDao
import dev.smoketrees.twist.db.AnimeDetailsDao
import dev.smoketrees.twist.model.MAL.JikanSearchModel
import dev.smoketrees.twist.model.twist.AnimeDetails
import dev.smoketrees.twist.model.twist.AnimeDetailsEntity
import dev.smoketrees.twist.model.twist.Result

class AnimeRepo(
    val webClient: AnimeWebClient,
    private val MALClient: MALWebClient,
    private val animeDao: AnimeDao,
    private val episodeDao: AnimeDetailsDao
) : BaseRepo() {
    fun getAllAnime() = makeRequestAndSave(
        databaseQuery = { animeDao.getAllAnime() },
        networkCall = { webClient.getAllAnime() },
        saveCallResult = {
            animeDao.saveAnime(it)
        }
    )

    fun getTrendingAnime(limit: Int) = makeRequest {
        webClient.getTrendingAnime(limit)
    }

    fun getSeasonalAnime() = makeRequestAndSave(
        databaseQuery = { animeDao.getOngoingAnime() },
        networkCall = { webClient.getAllAnime() },
        saveCallResult = {

            it.forEach { x -> Log.d("REPO", x.toString()) }
            animeDao.saveAnime(it)
        }
    )

    fun getAnimeDetails(name: String, id: Int) = makeRequestAndSave(
        databaseQuery = { episodeDao.getAnimeDetails(id) },
        networkCall = {
            saveEpisodeDetails(
                webClient.getAnimeDetails(name),
                MALClient.getAnimeByName(name)
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
        MALClient.getAnimeByName(animeName)
    }

    fun getMALAnimeById(id: Int) = makeRequest {
        MALClient.getAnimeById(id)
    }

    fun searchAnime(animeName: String) = animeDao.searchAnime(animeName)

    fun kitsuRequest(pageLimit: Int, sort: String, offset: Int) = makeRequest {
        webClient.kitsuRequest(pageLimit, sort, offset)
    }
}