package dev.smoketrees.twist.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.smoketrees.twist.model.twist.Episode

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episode WHERE animeId = :animeId")
    fun getEpisodesForAnime(animeId: Int): LiveData<List<Episode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEpisodes(vararg episodes: Episode)

    @Delete
    suspend fun deleteEpisodes(episode: Episode)
}