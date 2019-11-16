package dev.smoketrees.twist.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.smoketrees.twist.model.twist.AnimeDetailsEntity
import dev.smoketrees.twist.model.twist.Episode

@Dao
interface AnimeDetailsDao {
//    @Query("SELECT * FROM episode WHERE animeId = :animeId")
//    fun getEpisodesForAnime(animeId: Int): LiveData<List<Episode>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun saveEpisodes(episodes: List<Episode>)
//
//    @Delete
//    suspend fun deleteEpisodes(episode: Episode)

    @Query("SELECT * FROM animedetailsentity WHERE anime_id = :animeId")
    fun getAnimeDetails(animeId: Int): LiveData<AnimeDetailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAnimeDetails(animeDetailsEntity: AnimeDetailsEntity)
}