package dev.smoketrees.twist.ui.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import dev.smoketrees.twist.api.anime.AnimeWebClient
import dev.smoketrees.twist.repository.AnimeRepo

class PlayerViewModel(private val repo: AnimeRepo) : ViewModel() {
    fun getAnimeSources(animeName: String) = repo.getAnimeSources(animeName)

    var playWhenReady = true
    var currentWindowIndex = 0
    var playbackPosition = 0L
    var currUri: Uri? = null
    var referer = ""
    var portrait = false

    var downloadID = 0L

}
