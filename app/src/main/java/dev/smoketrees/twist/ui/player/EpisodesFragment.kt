package dev.smoketrees.twist.ui.player


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.EpisodeListAdapter
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.base.BaseFragment
import dev.smoketrees.twist.ui.home.MainActivity
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_episodes.*

class EpisodesFragment :
    BaseFragment<EpisodesViewModel>(R.layout.fragment_episodes, EpisodesViewModel::class) {

    private val args: EpisodesFragmentArgs by navArgs()

    private val fab by lazy { (requireActivity() as MainActivity).scroll_fab }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EpisodeListAdapter(requireActivity()) { ep, shouldDownload ->
            val action = EpisodesFragmentDirections.actionEpisodesFragmentToAnimePlayerActivity(args.slugName, ep.number!!, shouldDownload)
            findNavController().navigate(action)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        episode_list.adapter = adapter
        episode_list.layoutManager = layoutManager
        anime_description.movementMethod = ScrollingMovementMethod()

        fab.show()
        // FAB to scroll to very bottom
        fab.setOnClickListener {
            episode_list.smoothScrollToPosition(adapter.itemCount - 1)
        }
        adapter.onBottomReachedListener = {
            fab.hide()
        }

        viewModel.getAnimeDetails(args.slugName, args.id).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.LOADING -> {
                    episode_spinkit.show()
                    episode_list.hide()
                    anime_image.hide()
                    anime_title.hide()
                    anime_description.hide()
                    anime_rating.hide()
                    anime_episodes.hide()
                    anime_ongoing_text.hide()
                    fab.hide()
                }

                Result.Status.SUCCESS -> {
                    it.data?.let { detailsEntity ->
                        anime_title.text = detailsEntity.title
                        anime_episodes.text = "${detailsEntity.episodeList.size} episodes"
                        anime_description.text = detailsEntity.synopsis
                        anime_rating.text = "Score: ${detailsEntity.score}%"
                        detailsEntity.airing?.let { ongoing ->
                            if (ongoing) anime_ongoing_text.show() else anime_ongoing_text.hide()
                        }
                        Glide.with(requireContext())
                            .load(detailsEntity.imageUrl)
                            .into(anime_image)

                        episode_spinkit.hide()
                        episode_list.show()
                        anime_image.show()
                        anime_title.show()
                        anime_description.show()
                        anime_rating.show()
                        anime_episodes.show()

                        if (detailsEntity.episodeList.isNotEmpty()) {
                            adapter.updateData(it.data.episodeList)
                        }

                        if (layoutManager.findLastCompletelyVisibleItemPosition() < adapter.itemCount - 1) {
                            fab.show()
                        }
                    }
                }

                Result.Status.ERROR -> {
                    toast(it.message!!)
                    episode_list.hide()
                }
            }
        })
    }

    override fun onDestroyView() {
        fab.hide()
        super.onDestroyView()
    }

}
