package dev.smoketrees.twist.model.twist

data class LibraryEpisode(
    val id: Number,
    val user_id: Number,
    val anime_id: Number,
    val episode_id: Number,
    val progress: Number,
    val completed: Number,
    val watched_at: String,
    val created_at: String,
    val updated_at: String
)