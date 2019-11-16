package dev.smoketrees.twist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.smoketrees.twist.model.twist.AnimeDetailsEntity
import dev.smoketrees.twist.model.twist.AnimeItem

@Database(entities = [AnimeItem::class, AnimeDetailsEntity::class], version = 1)
@TypeConverters(EpisodeListTypeConverter::class)
abstract class AnimeDb : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
    abstract fun episodeDao(): AnimeDetailsDao
}