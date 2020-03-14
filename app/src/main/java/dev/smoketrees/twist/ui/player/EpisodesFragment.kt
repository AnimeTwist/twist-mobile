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
import dev.smoketrees.twist.BR
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.EpisodeListAdapter
import dev.smoketrees.twist.databinding.FragmentEpisodesBinding
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.base.BaseFragment
import dev.smoketrees.twist.ui.home.MainActivity
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.activity_main.*

class EpisodesFragment :
    BaseFragment<FragmentEpisodesBinding, EpisodesViewModel>(
        R.layout.fragment_episodes,
        EpisodesViewModel::class
    ) {

    override val bindingVariable: Int = BR.episodeViewModel

    private val args: EpisodesFragmentArgs by navArgs()

    private val fab by lazy { (requireActivity() as MainActivity).scroll_fab }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EpisodeListAdapter(requireActivity()) { ep, shouldDownload ->
            val action = EpisodesFragmentDirections.actionEpisodesFragmentToAnimePlayerActivity(
                args.slugName,
                ep.number!!,
                shouldDownload
            )
            findNavController().navigate(action)
        }

        val layoutManager = LinearLayoutManager(requireContext())

        dataBinding.episodeList.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
        }
        dataBinding.animeDescription.movementMethod = ScrollingMovementMethod()

        fab.show()
        // FAB to scroll to very bottom
        fab.setOnClickListener {
            dataBinding.episodeList.smoothScrollToPosition(adapter.itemCount - 1)
        }
        adapter.onBottomReachedListener = {
            fab.hide()
        }

        viewModel.getAnimeDetails(args.slugName, args.id).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.LOADING -> {
                    showLoader()
                    dataBinding.hasResult = false
                    fab.hide()
                }

                Result.Status.SUCCESS -> {
                    it.data?.let { detailsEntity ->
                        dataBinding.apply {
                            animeTitle.text = detailsEntity.title
                            animeEpisodes.text = "${detailsEntity.episodeList.size} episodes"
                            animeDescription.text = detailsEntity.synopsis
                            animeRating.text = "Score: ${detailsEntity.score}%"
                        }

                        detailsEntity.airing?.let { ongoing ->
                            if (ongoing) dataBinding.animeOngoingText.show() else dataBinding.animeOngoingText.hide()
                        }
                        Glide.with(requireContext())
                            .load(detailsEntity.imageUrl)
                            .into(dataBinding.animeImage)

                        hideLoader()
                        dataBinding.hasResult = true

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
                    dataBinding.episodeList.hide()
                }
            }
        })
    }

    override fun onDestroyView() {
        fab.hide()
        super.onDestroyView()
    }

}
