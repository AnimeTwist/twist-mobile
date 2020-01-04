package dev.smoketrees.twist.model.twist

import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity
data class NejireExtension(
    val cover_image: String,
    val poster_image: String
)