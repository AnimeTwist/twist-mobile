package dev.smoketrees.twist.di.module

import androidx.room.Room
import dev.smoketrees.twist.db.AnimeDb
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val roomModule = module {
    single {
        Room.databaseBuilder(androidApplication(), AnimeDb::class.java, "anime-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AnimeDb>().animeDao() }
    single { get<AnimeDb>().episodeDao() }
    single { get<AnimeDb>().trendingAnimeDao() }
}