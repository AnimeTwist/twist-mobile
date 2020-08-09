package dev.smoketrees.twist.model.twist

import androidx.room.Embedded
import androidx.room.Relation

data class AnimeWithEpisodes(
    @Embedded val animeItem: AnimeItem,
    @Relation(
        parentColumn = "uid",
        entityColumn = "anime_id"
    )
    val watchedEpisodes: List<LibraryEpisode>
)