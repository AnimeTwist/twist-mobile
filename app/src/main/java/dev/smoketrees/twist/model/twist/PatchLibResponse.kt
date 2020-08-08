package dev.smoketrees.twist.model.twist

data class PatchLibResponse (
    val id: Long,
    val userID: Long,
    val animeID: Long,
    val episodeID: Long,
    val progress: Double,
    val completed: Boolean,
    val watchedAt: String,
    val createdAt: String,
    val updatedAt: String
)