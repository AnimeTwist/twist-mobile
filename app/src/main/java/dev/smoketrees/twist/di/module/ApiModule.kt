package dev.smoketrees.twist.di.module

import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.api.anime.AnimeWebService
import dev.smoketrees.twist.api.getOkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TWIST_BASE_URL = "https://twist.suzuha.moe/"
private const val TWIST_API = "TWIST_API"

val apiModule = module {
    factory { getOkHttpClient(androidContext()) }

    single(named(TWIST_API)) {
        Retrofit.Builder()
            .baseUrl(TWIST_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    factory { get<Retrofit>(named(TWIST_API)).create(AnimeWebService::class.java) }

    factory { AnimeWebClient(get()) }
}