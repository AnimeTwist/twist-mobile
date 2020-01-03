package dev.smoketrees.twist.di.module

import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.api.anime.AnimeWebService
import dev.smoketrees.twist.api.getOkHttpClient
import dev.smoketrees.twist.api.jikan.MALWebClient
import dev.smoketrees.twist.api.jikan.MALWebService
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TWIST_BASE_URL = "https://nejire.deletescape.cloud/"
private const val MAL_BASE_URL = "https://suzuha.deletescape.cloud/"
private const val TWIST_API = "TWIST_API"
private const val MAL_API = "MAL_API"

val apiModule = module {
    factory { getOkHttpClient(androidContext()) }

    single(named(TWIST_API)) {
        Retrofit.Builder()
            .baseUrl(TWIST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    single(named(MAL_API)) {
        Retrofit.Builder()
            .baseUrl(MAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    factory { get<Retrofit>(named(TWIST_API)).create(AnimeWebService::class.java) }
    factory { get<Retrofit>(named(MAL_API)).create(MALWebService::class.java) }

    factory { AnimeWebClient(get()) }
    factory { MALWebClient(get()) }
}