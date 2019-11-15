package dev.smoketrees.twist.ui.player

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.CryptoHelper
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.activity_anime_player.*
import org.koin.android.viewmodel.ext.android.viewModel

class AnimePlayerActivity : AppCompatActivity() {

    private val args: AnimePlayerActivityArgs by navArgs()
    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_player)
        val slug = args.slugName!!
        val epNo = args.episodeNo

        viewModel.referer = "https://twist.moe/a/$slug/$epNo"

        player = ExoPlayerFactory.newSimpleInstance(this)
        player_view.player = player

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

        player.prepare(mediaSource, true, false)
    }

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this)
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

    private fun saveState() {
        viewModel.playbackPosition = player.currentPosition
        viewModel.currentWindowIndex = player.currentWindowIndex
        viewModel.playWhenReady = player.playWhenReady
    }

    private fun loadState() {
        player.apply {
            playWhenReady = viewModel.playWhenReady
            seekTo(viewModel.currentWindowIndex, viewModel.playbackPosition)
        }
    }

    private fun releasePlayer() {
        saveState()
        player.release()
    }

    private fun start() {
        player.playWhenReady = true
        player.playbackState
        viewModel.currUri?.let {
            play(it)
        }
    }

    private fun resume() {
        player.playWhenReady = true
        viewModel.currUri?.let {
            play(it)
        }
        loadState()
    }

    private fun pause() {
        player.playWhenReady = false
        player.playbackState
        saveState()
    }

    private fun stop() {
        releasePlayer()
    }

    override fun onStart() {
        super.onStart()
        start()
    }

    override fun onStop() {
        super.onStop()
        stop()
    }

    override fun onResume() {
        super.onResume()
        resume()
    }

    override fun onPause() {
        super.onPause()
        pause()
    }
}
