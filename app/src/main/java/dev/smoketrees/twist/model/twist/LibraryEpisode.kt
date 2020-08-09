package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class LibraryEpisode(
    val id: Int,
    val user_id: Int,
    @ColumnInfo(name = "anime_id") val anime_id: Int,
    @PrimaryKey val episode_id: Int,
    val progress: Double,
    val completed: Int,
    val watched_at: String,
    val created_at: String,
    val updated_at: String
)