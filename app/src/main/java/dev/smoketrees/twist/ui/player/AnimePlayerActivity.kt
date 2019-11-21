package dev.smoketrees.twist.ui.player

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.CryptoHelper
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.activity_anime_player.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import org.koin.android.viewmodel.ext.android.viewModel

class AnimePlayerActivity : AppCompatActivity(), SessionAvailabilityListener {
    override fun onCastSessionAvailable() {
        player = castPlayer
        media_button.show()
    }

    override fun onCastSessionUnavailable() {
        player = exoPlayer
        media_button.hide()
    }

    private val args: AnimePlayerActivityArgs by navArgs()
    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var castPlayer: CastPlayer
    private lateinit var player: Player
    private lateinit var castContext: CastContext

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_player)
        CastButtonFactory.setUpMediaRouteButton(this, media_button)
        castContext = CastContext.getSharedInstance(this)!!

        castPlayer = CastPlayer(castContext)
        castPlayer.setSessionAvailabilityListener(this)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this)

        if (castContext.castState != CastState.NO_DEVICES_AVAILABLE) {
            media_button.show()
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        exo_rotate_icon.setOnClickListener {
            if (viewModel.portrait) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                viewModel.portrait = false
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                viewModel.portrait = true
            }
        }

        val slug = args.slugName!!
        val epNo = args.episodeNo

        viewModel.referer = "https://twist.moe/a/$slug/$epNo"

        if (viewModel.currUri == null) {
            viewModel.getAnimeSources(slug).observe(this, Observer {
                when (it.status) {
                    Result.Status.LOADING -> {
                        // TODO: Hide/show some shit
                    }

                    Result.Status.SUCCESS -> {
                        val decryptedUrl =
                            CryptoHelper.decryptSourceUrl(this, it?.data?.get(epNo - 1)?.source!!)

                        play(Uri.parse("https://twist.moe${decryptedUrl}"))

                    }

                    Result.Status.ERROR -> {
                        toast(it.message!!)
                    }
                }
            })
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

    private fun preparePlayer(uri: Uri) {
        val sourceFactory = DefaultHttpDataSourceFactory(
            Util.getUserAgent(
                this,
                "twist.moe"
            )
        )

        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        progressBar.show()
                    }

                    Player.STATE_READY -> {
                        progressBar.hide()
                    }
                }
            }
        })

        val mediaSource = ProgressiveMediaSource.Factory {
            val dataSource = sourceFactory.createDataSource()
            dataSource.setRequestProperty("Referer", viewModel.referer)
            dataSource
        }.createMediaSource(uri)

        if (player == exoPlayer) {
            exoPlayer.prepare(mediaSource, true, false)
        } else {
            castContext.sessionManager.currentCastSession
            val mediaInfo = MediaInfo.Builder()

        }
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

    private fun play(uri: Uri) {
        viewModel.currUri = uri
        initializePlayer()
        preparePlayer(uri)
        hideSystemUi()
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
        castPlayer.release()
        exoPlayer.release()
    }
}
