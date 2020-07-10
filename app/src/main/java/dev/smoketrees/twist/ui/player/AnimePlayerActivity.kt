package dev.smoketrees.twist.ui.player

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dev.smoketrees.twist.BuildConfig
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.CryptoHelper
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.activity_anime_player.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import org.koin.android.viewmodel.ext.android.viewModel


class AnimePlayerActivity : AppCompatActivity() {

    private val args: AnimePlayerActivityArgs by navArgs()
    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var player: ExoPlayer

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

        val slug = args.slugName
        val name = args.displayName
        val epNo = args.episodeNo
        val shouldDownload = args.shouldDownload

        // Show information about episode
        exo_current_episode.text = String.format(resources.getString(R.string.episode_info), epNo)
        exo_ep_info.text = name

        Log.d("Should_download", shouldDownload.toString())

        val referer = "https://twist.moe/a/$slug/$epNo"
        viewModel.referer = referer

        if (shouldDownload) {
            viewModel.getAnimeSources(slug).observe(this, Observer {
                when (it.status) {
                    Result.Status.LOADING -> {
                        // TODO: Hide/show some shit
                    }

                    Result.Status.SUCCESS -> {
                        if (it != null) {
                            if (!it.data.isNullOrEmpty()) {
                                val decryptedUrl =
                                    it.data[epNo - 1].source?.let { src ->
                                        CryptoHelper.decryptSourceUrl(src)
                                    }

                                val downloadUrl =
                                    Uri.parse("${BuildConfig.CDN_URL}${decryptedUrl}")
                                val downloadManager =
                                    getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                                val request = DownloadManager.Request(downloadUrl)
                                    .setTitle("Downloading $slug-$epNo")
                                    .setDescription("Downloading episode $epNo")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    .setDestinationInExternalPublicDir(
                                        Environment.DIRECTORY_MOVIES,
                                        "$slug-episode-$epNo.mkv"
                                    )
                                    .addRequestHeader("Referer", referer)

                                Log.d("DOWNLOAD", downloadUrl.toString())

                                viewModel.downloadID = downloadManager.enqueue(request)
                                finish()
                            }
                        }
                    }

                    Result.Status.ERROR -> {
                        toast(it.message.toString())
                    }
                }
            })
        }

        player = SimpleExoPlayer.Builder(this).build()

        if (viewModel.currUri == null) {
            viewModel.getAnimeSources(slug).observe(this, Observer {
                when (it.status) {
                    Result.Status.LOADING -> {
                        // TODO: Hide/show some shit
                    }

                    Result.Status.SUCCESS -> {
                        if (it != null) {
                            if (!it.data.isNullOrEmpty()) {
                                val decryptedUrl =
                                    it.data[epNo - 1].source?.let { src ->
                                        CryptoHelper.decryptSourceUrl(
                                            src
                                        )
                                    }
                                play(Uri.parse("https://twistcdn.bunny.sh${decryptedUrl}"))
                            }
                        }
                    }

                    Result.Status.ERROR -> {
                        toast(it.message.toString())
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
                    Player.STATE_ENDED -> {
                        finish()
                    }
                    Player.STATE_IDLE -> {
                        // do nothing
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
        player.release()
    }
}
