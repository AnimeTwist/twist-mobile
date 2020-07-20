package dev.smoketrees.twist.ui.player

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.smoketrees.twist.model.twist.AnimeSource
import dev.smoketrees.twist.repository.AnimeRepo

class PlayerViewModel(private val repo: AnimeRepo) : ViewModel() {
    fun getAnimeSources(animeName: String) = repo.getAnimeSources(animeName)

    var playWhenReady = true
    var currentWindowIndex = 0
    var playbackPosition = 0L
    var currEp = MutableLiveData<Int>(null)
    var sources: List<AnimeSource>? = null
    var orientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}
