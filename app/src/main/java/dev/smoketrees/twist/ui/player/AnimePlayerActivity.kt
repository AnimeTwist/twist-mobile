package dev.smoketrees.twist.ui.player

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.DiscontinuityReason
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.CryptoHelper
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.activity_anime_player.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class AnimePlayerActivity : AppCompatActivity() {

    private val args: AnimePlayerActivityArgs by navArgs()
    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var player: ExoPlayer
    private lateinit var concatenatedSource: ConcatenatingMediaSource

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_player)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        exo_rotate_icon.setOnClickListener {
            when (viewModel.orientation) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                    viewModel.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    requestedOrientation = viewModel.orientation
                }
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                    viewModel.orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    requestedOrientation = viewModel.orientation
                }
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                    viewModel.orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    requestedOrientation = viewModel.orientation
                }
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> {
                    viewModel.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    requestedOrientation = viewModel.orientation
                }
            }
        }

        exo_ep_info!!.text = args.displayName
        exo_back!!.setOnClickListener { finish() }

        concatenatedSource = ConcatenatingMediaSource()
        player = SimpleExoPlayer.Builder(this).build()

        // Get anime sources
        viewModel.getAnimeSources(args.slugName).observe(this, Observer {
            when (it.status) {
                Result.Status.LOADING -> {
                    // TODO: Hide/show some shit
                }

                Result.Status.SUCCESS -> {
                    viewModel.sources = it.data

                    // Load episode and add next episode to playlist
                    setupAnimeSource(args.episodeNo)
                    viewModel.currEp.value = args.episodeNo

                    // initialize player
                    initializePlayer()
                    preparePlayer()
                    hideSystemUi()

                    viewModel.currEp.observe(this, Observer {
                        exo_current_episode.text = String.format(resources.getString(R.string.episode_info), it)
                        // Add next episode
                        setupAnimeSource((it + 1) % viewModel.sources!!.size)
                    })
                }

                Result.Status.ERROR -> {
                    toast(it.message!!.msg)
                }
            }
        })

    }

    private fun setupAnimeSource(epNo: Int) {
        if (!viewModel.sources.isNullOrEmpty()) {
            val decryptedUrl =
                    viewModel.sources!![epNo - 1].source?.let { src ->
                        CryptoHelper.decryptSourceUrl(
                                src
                        )
                    }
            val sourceFactory = DefaultHttpDataSourceFactory(
                    Util.getUserAgent(
                            this,
                            "twist.moe"
                    )
            )

            // Add mediaSource to playlist
            concatenatedSource.addMediaSource(ProgressiveMediaSource.Factory {
                val dataSource = sourceFactory.createDataSource()
                dataSource.setRequestProperty("Referer", "https://twist.moe/a/${args.slugName}/$epNo")
                dataSource
            }.createMediaSource(Uri.parse("https://twistcdn.bunny.sh${decryptedUrl}")))
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        player_view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun preparePlayer() {

        val remainingHandler = Handler()
        val getRemaining: Runnable = object : Runnable {
            override fun run() {
                val duration = player.duration
                val position = player.currentPosition
                if (duration > 0)  {
                    val remaining = duration - position
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining)
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(remaining - TimeUnit.MINUTES.toMillis(minutes))
                    val finalString = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
                    exo_remaining!!.text = finalString

                    // Remove scheduled updates.
                    remainingHandler.removeCallbacks(this)

                    // Schedule an update if necessary.
                    if (player.playbackState != Player.STATE_IDLE && player.playbackState != Player.STATE_ENDED) {
                        var delayMs: Long
                        if (player.playWhenReady && player.playbackState == Player.STATE_READY) {
                            delayMs = 1000 - position % 1000
                            if (delayMs < 200) {
                                delayMs += 1000
                            }
                        } else {
                            delayMs = 1000
                        }
                        remainingHandler.postDelayed(this, delayMs)
                    }
                    // else die
                }
            }
        }

        var lastSavedWindow = 0
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        // do nothing
                    }

                    Player.STATE_READY -> {
                        // Start calculating remaining time
                        remainingHandler.post(getRemaining)
                    }
                    Player.STATE_ENDED -> {
                        finish()
                    }
                    Player.STATE_IDLE -> {
                        // do nothing
                    }
                }
            }

            override fun onPositionDiscontinuity(@DiscontinuityReason reason: Int) {
                super.onPositionDiscontinuity(reason)
                if (lastSavedWindow != player.currentWindowIndex) {
                    concatenatedSource.removeMediaSource(0)
                    player.seekTo(0,0)
                    viewModel.currEp.value = (viewModel.currEp.value!! + 1) % viewModel.sources!!.size
                }
                lastSavedWindow = player.currentWindowIndex
            }
        })

        player.prepare(concatenatedSource, true, false)
    }

    private fun initializePlayer() {
//        val loadControl = DefaultLoadControl.Builder()
//            .setBufferDurationsMs(
//                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
//                10 * 50 * 1000,
//                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
//                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
//            ).createDefaultLoadControl()
        player_view.player = player
        player.playWhenReady = viewModel.playWhenReady
        player.seekTo(viewModel.currentWindowIndex, viewModel.playbackPosition)
    }

    override fun onStart() {
        super.onStart()
        player.seekTo(viewModel.playbackPosition)
        player.playWhenReady = viewModel.playWhenReady
    }

    override fun onStop() {
        super.onStop()
        viewModel.playWhenReady = player.playWhenReady
        viewModel.playbackPosition = player.currentPosition
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }
}
