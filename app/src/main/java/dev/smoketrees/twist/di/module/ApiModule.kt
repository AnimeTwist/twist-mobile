package dev.smoketrees.twist.di.module

import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.api.anime.AnimeWebService
import dev.smoketrees.twist.api.getOkHttpClient
import dev.smoketrees.twist.api.jikan.JikanWebClient
import dev.smoketrees.twist.api.jikan.JikanWebService
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TWIST_BASE_URL = "https://twist.moe/api/"
private const val JIKAN_BASE_URL = "https://api.jikan.moe/v3/"
private const val TWIST_API = "TWIST_API"
private const val JIKAN_API = "JIKAN_API"

val apiModule = module {
    factory { getOkHttpClient(androidContext()) }

    single(named(TWIST_API)) {
        Retrofit.Builder()
            .baseUrl(TWIST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    single(named(JIKAN_API)) {
        Retrofit.Builder()
            .baseUrl(JIKAN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    factory { get<Retrofit>(named(TWIST_API)).create(AnimeWebService::class.java) }
    factory { get<Retrofit>(named(JIKAN_API)).create(JikanWebService::class.java) }

    factory { AnimeWebClient(get()) }
    factory { JikanWebClient(get()) }
}