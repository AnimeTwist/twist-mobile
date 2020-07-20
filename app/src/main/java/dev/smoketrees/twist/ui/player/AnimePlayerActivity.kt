package dev.smoketrees.twist.ui.player

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.CryptoHelper
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.activity_anime_player.*
import kotlinx.android.synthetic.main.exo_at_player_view.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class AnimePlayerActivity : AppCompatActivity() {

    private val args: AnimePlayerActivityArgs by navArgs()
    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var player: ExoPlayer
    private lateinit var concatenatedSource: ConcatenatingMediaSource
    lateinit var mediaSession: MediaSessionCompat
    lateinit var mediaSessionConnector: MediaSessionConnector
    private var paused = false
    private var skipFlag = false
    private val controllerHandler = Handler()

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

        exo_ep_info.text = args.displayName
        exo_back.setOnClickListener { finish() }

        concatenatedSource = ConcatenatingMediaSource()
        player = SimpleExoPlayer.Builder(this).build()
        initializePlayer()
        preparePlayer()
        hideSystemUi()

        // Connect to media session
        mediaSession = MediaSessionCompat(this, packageName)
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(player)

        // Use player effects
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {

            override fun onSkipToNext() {
                player.seekTo(1, 0)
                super.onSkipToNext()
            }

            override fun onPause() {
                pause()
                super.onPause()
            }

            override fun onPlay() {
                play()
                super.onPlay()
            }

            override fun onStop() {
                pause()
                super.onStop()
            }
        })

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)


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

                    viewModel.currEp.observe(this, Observer { ep ->
                        exo_current_episode.text = String.format(resources.getString(R.string.episode_info), ep)
                        // Add next episode
                        setupAnimeSource((ep + 1) % viewModel.sources!!.size)
                    })
                }

                Result.Status.ERROR -> {
                    when (it.message!!.code) {
                        111 -> showError("ERR_INTERNET_CONN")
                    }
                    toast(it.message.msg)
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

    private fun showError(message: String) {
        exo_controller.visibility = View.VISIBLE
        controllerHandler.removeCallbacksAndMessages(null)
        errorText.text = message
        err_notice.visibility = View.VISIBLE
    }

    private fun showNotice() {
        skip_notice.alpha = (0f)
        skip_notice.translationY = 100f
        skip.isClickable = true
        cancel.isClickable = true
        skip_notice.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setListener(null)
    }

    private fun hideNotice(adapter: AnimatorListenerAdapter? = null) {
        skip_notice.alpha = (1f)
        skip_notice.translationY = 0f
        skip.isClickable = false
        cancel.isClickable = false
        skip_notice.animate()
                .alpha(0f)
                .translationY(100f)
                .setDuration(300)
                .setListener(adapter)
    }

    private fun pause() {
        controllerHandler.removeCallbacksAndMessages(null)
        player.playWhenReady = false
        exo_controller.visibility = View.VISIBLE
        state_indicator.visibility = View.VISIBLE
        indicator_icon.setImageResource(R.drawable.ic_at_pause)

        if (player.playbackState == Player.STATE_BUFFERING) {
            player_view.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)

            // Morph from buffer
            val anim = AnimatedVectorDrawableCompat.create(
                this,
                R.drawable.ic_at_state_buffer_in
            )
            indicator_base.setImageDrawable(anim)
            anim!!.start()
        }
        else {
            // Scale in
            state_indicator.requestLayout()
            state_indicator.animation =  AnimationUtils.loadAnimation(this, R.anim.scale_in)
            state_indicator.animate().start()

            // Morph from square
            val anim = AnimatedVectorDrawableCompat.create(
                this,
                R.drawable.ic_at_state_in
            )
            indicator_base.setImageDrawable(anim)
            anim!!.start()
        }
        paused = true
        if (skipFlag && exo_controller.visibility == View.INVISIBLE) showNotice()
    }

    private fun play() {
        hideSystemUi()
        player.playWhenReady = true

        fun ready() {
            state_indicator.visibility = View.INVISIBLE
            player_view.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        }

        if (player.playbackState == Player.STATE_BUFFERING) {
            // Morph into buffer
            val anim = AnimatedVectorDrawableCompat.create(
                this,
                R.drawable.ic_at_state_buffer_out
            )
            indicator_base.setImageDrawable(anim)
            anim!!.registerAnimationCallback(object: Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    ready()
                }
            })
            anim.start()
        }
        else {
            indicator_icon.setImageResource(R.drawable.ic_at_play)
            // Scale out
            state_indicator.requestLayout()
            state_indicator.animation =  AnimationUtils.loadAnimation(this, R.anim.scale_out)
            state_indicator.animate().start()

            // Morph into square
            val anim = AnimatedVectorDrawableCompat.create(
                this,
                R.drawable.ic_at_state_out
            )
            indicator_base.setImageDrawable(anim)
            anim!!.registerAnimationCallback(object: Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    ready()
                }
            })
            anim.start()
        }
        paused = false

        if (player.playbackError == null && concatenatedSource.size != 0) {
            controllerHandler.postDelayed({
                exo_controller.visibility = View.INVISIBLE
                if (skipFlag) hideNotice()
            }, 3000)
        }
    }

    private fun preparePlayer() {

        val remainingHandler = Handler()
        val getRemaining: Runnable = object : Runnable {
            override fun run() {
                val duration = player.duration
                val position = player.currentPosition
                if (duration > 0)  {
                    val remaining = duration - position
                    if (skipFlag != remaining <= 180000) {
                        skipFlag = remaining <= 180000
                        if (skipFlag && exo_controller.visibility == View.VISIBLE) showNotice()
                        if (!skipFlag) hideNotice()
                    }
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining)
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(remaining - TimeUnit.MINUTES.toMillis(minutes))
                    val finalString = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
                    exo_remaining!!.text = finalString
                    if (skipFlag) skipText.text = String.format(resources.getString(R.string.next_episode_countdown), finalString)

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

        player_view.setOnClickListener {
            if (player.playWhenReady && (player.isPlaying || player.playbackState == Player.STATE_BUFFERING)) pause()
            else play()
        }
        exo_play.setOnClickListener { play() }
        exo_pause.setOnClickListener { pause() }
        skip.setOnClickListener { player.seekTo(1,0) }
        cancel.setOnClickListener {
            hideNotice(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    skip_notice.visibility = View.INVISIBLE
                }
            })
        }
        retry.setOnClickListener { recreate() }
        skip.isClickable = false
        cancel.isClickable = false

        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        controllerHandler.removeCallbacksAndMessages(null)
                        exo_controller.visibility = View.VISIBLE
                        if (skipFlag) showNotice()
                        if (!paused) state_indicator.visibility = View.INVISIBLE
                    }

                    Player.STATE_READY -> {
                        // Start calculating remaining time
                        remainingHandler.post(getRemaining)
                        err_notice.visibility = View.GONE
                        if (player.isPlaying) play()
                    }
                    else -> { /* Do nothing */ }
                }
            }

            override fun onPositionDiscontinuity(@Player.DiscontinuityReason reason: Int) {
                super.onPositionDiscontinuity(reason)
                if (reason == Player.DISCONTINUITY_REASON_SEEK && paused) play()
                if (reason == Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT && paused) play()
                if (lastSavedWindow != player.currentWindowIndex) {
                    if (!skipFlag) skip_notice.visibility = View.INVISIBLE
                    concatenatedSource.removeMediaSource(0)
                    player.seekTo(0,0)
                    viewModel.currEp.value = (viewModel.currEp.value!! + 1) % viewModel.sources!!.size
                    hideNotice(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            skip_notice.visibility = View.VISIBLE
                        }
                    })
                    skipFlag = false
                }
                lastSavedWindow = player.currentWindowIndex
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                controllerHandler.removeCallbacksAndMessages(null)

                when (error.type) {
                    ExoPlaybackException.TYPE_SOURCE -> {
                        showError("MEDIA_ERR_DECODE")
                    }
                    ExoPlaybackException.TYPE_REMOTE -> {
                        showError("MEDIA_ERR_ABORTED")
                    }
                    ExoPlaybackException.TYPE_OUT_OF_MEMORY -> {
                        showError("ERR_OUT_OF_MEMORY")
                    }
                    ExoPlaybackException.TYPE_RENDERER -> {
                        showError("MEDIA_ERR_RENDER")
                    }
                    ExoPlaybackException.TYPE_UNEXPECTED -> {
                        showError("Could not load video")
                        toast(error.message.toString())
                    }
                }
            }
        })

        player.prepare(concatenatedSource, true, false)
    }

    private fun initializePlayer() {
        player_view.player = player
        player.playWhenReady = viewModel.playWhenReady
        player.seekTo(viewModel.currentWindowIndex, viewModel.playbackPosition)
    }

    override fun onStart() {
        super.onStart()
        player.seekTo(viewModel.playbackPosition)
        if (!viewModel.playWhenReady) pause()
        else play()
        mediaSessionConnector.setPlayer(player)
        mediaSession.isActive = true
    }

    override fun onStop() {
        super.onStop()
        viewModel.playWhenReady = player.playWhenReady
        viewModel.playbackPosition = player.currentPosition
        if (!paused) pause()
        mediaSessionConnector.setPlayer(null)
        mediaSession.isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        player.release()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                if (paused) play()
            }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                if (!paused) pause()
            }
            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                player.seekTo(1,0)
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
