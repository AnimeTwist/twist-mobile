package dev.smoketrees.twist.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.smoketrees.twist.model.twist.TrendingAnimeItem

@Dao
interface TrendingAnimeDao {
    @Query("SELECT * FROM trendinganimeitem")
    fun getTrendingAnime(): LiveData<List<TrendingAnimeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrendingAnime(animeItems: List<TrendingAnimeItem>)
}