package dev.smoketrees.twist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.smoketrees.twist.model.twist.AnimeDetailsEntity
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.LibraryEpisode
import dev.smoketrees.twist.model.twist.TrendingAnimeItem

@Database(
    entities = [AnimeItem::class, AnimeDetailsEntity::class, TrendingAnimeItem::class, LibraryEpisode::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(EpisodeListTypeConverter::class)
abstract class AnimeDb : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
    abstract fun episodeDao(): AnimeDetailsDao
    abstract fun trendingAnimeDao(): TrendingAnimeDao
}