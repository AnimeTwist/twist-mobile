package dev.smoketrees.twist.di.module

import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.api.anime.AnimeWebService
import dev.smoketrees.twist.api.getOkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://twist.moe/api/"

val apiModule = module {
    factory { getOkHttpClient(androidContext()) }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }

    factory { get<Retrofit>().create(AnimeWebService::class.java) }
    factory { AnimeWebClient(get()) }
}