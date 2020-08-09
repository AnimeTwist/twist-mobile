package dev.smoketrees.twist.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.AnimeWithEpisodes
import dev.smoketrees.twist.model.twist.LibraryEpisode

@Dao
interface AnimeDao {
    @Query("SELECT * FROM animeitem")
    fun getAllAnime(): LiveData<List<AnimeItem>>

    @Query("SELECT * FROM animeitem")
    fun getAllAnimeList(): List<AnimeItem>

    @Query("SELECT * FROM animeitem WHERE title LIKE :searchText OR altTitle LIKE :searchText")
    fun searchAnime(searchText: String): LiveData<List<AnimeItem>>

    @Query("SELECT * FROM animeitem WHERE id = :id")
    fun getAnimeById(id: Int): LiveData<AnimeItem>

    @Query("SELECT * FROM animeitem WHERE ongoing = 1")
    fun getOngoingAnime(): LiveData<List<AnimeItem>>

    @Query("SELECT * FROM animeitem WHERE ongoing = 1")
    fun getOngoingAnimeList(): List<AnimeItem>

    @Query("SELECT * FROM animeitem WHERE uid IN (:ids)")
    fun getAnimeByIds(ids: List<Int>): LiveData<List<AnimeItem>>

    @Transaction
    @Query("SELECT * FROM animeitem WHERE uid = :id")
    fun getWatchedEpisodes(id: Int): LiveData<AnimeWithEpisodes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWatchedEpisodes(episodes: List<LibraryEpisode>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAnime(animeItems: List<AnimeItem>)

    @Delete
    suspend fun deleteAnime(animeItem: AnimeItem)
}