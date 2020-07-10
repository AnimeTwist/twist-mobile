package dev.smoketrees.twist.repository


import android.util.Log
import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.db.AnimeDao
import dev.smoketrees.twist.db.AnimeDetailsDao
import dev.smoketrees.twist.db.TrendingAnimeDao
import dev.smoketrees.twist.model.twist.*

class AnimeRepo(
    val webClient: AnimeWebClient,
    private val animeDao: AnimeDao,
    private val episodeDao: AnimeDetailsDao,
    private val trendingAnimeDao: TrendingAnimeDao
) : BaseRepo() {
//    fun getAllAnime() = makeRequestAndSave(
//        databaseQuery = { animeDao.getAllAnime() },
//        networkCall = { webClient.getAllAnime() },
//        saveCallResult = { animeDao.saveAnime(it) }
//    )

    fun getAllDbAnime() = animeDao.getAllAnime()
    suspend fun getAllNetworkAnime() = webClient.getAllAnime()
    suspend fun saveAnime(animeItems: List<AnimeItem>) = animeDao.saveAnime(animeItems)
    suspend fun saveTrendingAnime(animeItems: List<TrendingAnimeItem>) =
        trendingAnimeDao.saveTrendingAnime(animeItems)


    suspend fun getNetworkTrendingAnime(limit: Int) = webClient.getTrendingAnime(limit)

    fun getDbTrendingAnime() = trendingAnimeDao.getTrendingAnime()

    fun getMotd() = makeRequest {
        webClient.getMotd()
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
                webClient.getAnimeDetails(name)
            )
        },
        saveCallResult = {
            episodeDao.saveAnimeDetails(it)
        }
    )

    private fun saveEpisodeDetails(
        episodeResult: Result<AnimeDetails>
    ): Result<AnimeDetailsEntity> {
        return if (episodeResult.status == Result.Status.SUCCESS) {
            Result.success(
                getAnimeDetailsEntity(episodeResult.data!!)
            )
        } else {
            Result.error("")
        }
    }

    // TODO: add support for this data to nejire and use it in the app
    private fun getAnimeDetailsEntity(
        episodeDetails: AnimeDetails
    ) = AnimeDetailsEntity(
        airing = episodeDetails.ongoing == 1,
        //endDate = result?.endDate,
        //episodes = result?.episodes,
        imageUrl = episodeDetails.extension?.posterImage,
        id = episodeDetails.id,
        //malId = result?.malId,
        //members = result?.members,
        //rated = result?.rated,
        score = episodeDetails.extension?.avgScore,
        //startDate = result?.startDate,
        synopsis = episodeDetails.description,
        title = episodeDetails.title,
        //type = result?.type,
        //url = result?.url,
        episodeList = episodeDetails.episodes!!
    )

    fun getAnimeSources(animeName: String) = makeRequest {
        webClient.getAnimeSources(animeName)
    }

    fun kitsuRequest(pageLimit: Int, sort: String, offset: Int) = makeRequest {
        webClient.kitsuRequest(pageLimit, sort, offset)
    }

    fun signIn(loginDetails: LoginDetails) = makeRequest {
        webClient.signIn(loginDetails)
    }

    fun signUp(registerDetails: RegisterDetails) = makeRequest {
        webClient.signUp(registerDetails)
    }
}