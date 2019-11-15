package dev.smoketrees.twist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Episode

@Database(entities = [AnimeItem::class, Episode::class], version = 1)
abstract class AnimeDb : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
    abstract fun episodeDao(): EpisodeDao
}