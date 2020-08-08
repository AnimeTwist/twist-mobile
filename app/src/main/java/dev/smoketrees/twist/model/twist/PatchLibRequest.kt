package dev.smoketrees.twist.model.twist

data class PatchLibRequest (
    val number: Long,
    val animeID: Long,
    val progress: Double,
    val watchedAt: String,
    val episodeID: Long,
    val completed: Boolean
)