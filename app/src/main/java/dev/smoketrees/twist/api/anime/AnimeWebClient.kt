package dev.smoketrees.twist.api.anime

import dev.smoketrees.twist.api.BaseApiClient
import dev.smoketrees.twist.model.twist.LoginDetails
import dev.smoketrees.twist.model.twist.PatchLibRequest
import dev.smoketrees.twist.model.twist.RegisterDetails

class AnimeWebClient(private val webService: AnimeWebService) : BaseApiClient() {
    suspend fun getAllAnime() = getResult {
        webService.getAllAnime()
    }

    suspend fun getAnimeDetails(animeName: String) = getResult {
        webService.getAnimeDetails(animeName)
    }

    suspend fun getAnimeSources(animeName: String) = getResult {
        webService.getAnimeSources(animeName)
    }

    suspend fun filteredKitsuRequest(
        pageLimit: Int,
        sort: String,
        filter: String,
        offset: Int
    ) = getResult {
        webService.filteredKitsuRequest(pageLimit, sort, filter, offset)
    }

    suspend fun kitsuRequest(
        pageLimit: Int,
        sort: String,
        offset: Int
    ) = getResult {
        webService.kitsuRequest(pageLimit, sort, offset)
    }

    suspend fun getTrendingAnime(limit: Int) = getResult {
        webService.getTrendingAnime(limit)
    }

    suspend fun getMotd() = getResult {
        webService.getMotd()
    }

    suspend fun signIn(loginDetails: LoginDetails) = getResult {
        webService.signIn(loginDetails)
    }

    suspend fun signUp(registerDetails: RegisterDetails) = getResult {
        webService.signUp(registerDetails)
    }

    suspend fun getUserLibrary(jwt: String) = getResult {
        webService.getUserLibrary(jwt)
    }

    suspend fun updateUserLibrary(jwt: String, body: PatchLibRequest) = getResult {
        webService.updateUserLibrary(jwt, body)
    }
}
