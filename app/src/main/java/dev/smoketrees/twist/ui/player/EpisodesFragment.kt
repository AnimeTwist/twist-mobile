package dev.smoketrees.twist.ui.player


import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import dev.smoketrees.twist.BR
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.EpisodeListAdapter
import dev.smoketrees.twist.databinding.FragmentEpisodesBinding
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.base.BaseFragment
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.ui.home.MainActivity
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class EpisodesFragment :
    BaseFragment<FragmentEpisodesBinding, EpisodesViewModel>(
        R.layout.fragment_episodes,
        EpisodesViewModel::class
    ) {

    override val bindingVariable: Int = BR.episodeViewModel

    private val args: EpisodesFragmentArgs by navArgs()
    private val animeViewModel by sharedViewModel<AnimeViewModel>()

    private val fab by lazy { (requireActivity() as MainActivity).scroll_fab }

    private val episodeAdapter by lazy {
        EpisodeListAdapter { ep, _ ->
            val action = EpisodesFragmentDirections.actionEpisodesFragmentToAnimePlayerActivity(
                args.slugName,
                dataBinding.anime!!.title!!,
                ep.number!!,
                false
            )
            findNavController().navigate(action)
        }
    }

    private val linearLayoutManager by lazy {
        GridLayoutManager(
            requireContext(),
            4
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getAnimeDetails(args.slugName, args.id)
            .observe(viewLifecycleOwner, Observer { animeDetails ->
                when (animeDetails.status) {
                    Result.Status.LOADING -> {
                        showLoader()
                        dataBinding.hasResult = false
                        fab.hide()
                    }

                    Result.Status.SUCCESS -> {
                        animeDetails.data?.let { detailsEntity ->
                            dataBinding.anime = detailsEntity

                            Glide.with(requireContext())
                                .load(detailsEntity.imageUrl)
                                .into(dataBinding.animeImage)

                            hideLoader()
                            dataBinding.hasResult = true


                            if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < episodeAdapter.itemCount - 1) {
                                fab.show()
                            }

                            animeViewModel.getWatchedEpisodes(args.id)
                                .observe(viewLifecycleOwner, Observer { animeWithEps ->
                                    if (detailsEntity.episodeList.isNotEmpty()) {
                                        episodeAdapter.updateData(animeDetails.data.episodeList)

                                        if (animeWithEps.watchedEpisodes.isNotEmpty()) {
                                            val epMap =
                                                animeWithEps.watchedEpisodes.map { it.episode_id to it }
                                                    .toMap()
                                            episodeAdapter.updateWatchedEps(epMap)
                                        }
                                    }
                                })
                        }
                    }

                    Result.Status.ERROR -> {
                        toast(animeDetails.message!!.msg)
                        dataBinding.episodeList.hide()
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.episodeList.apply {
            this.adapter = episodeAdapter
            this.layoutManager = linearLayoutManager
        }
        dataBinding.animeDescription.movementMethod = ScrollingMovementMethod()

        fab.show()
        // FAB to scroll to very bottom
        fab.setOnClickListener {
            dataBinding.episodeList.smoothScrollToPosition(episodeAdapter.itemCount - 1)
        }
        episodeAdapter.onBottomReachedListener = {
            fab.hide()
        }
    }

    override fun onDestroyView() {
        fab.hide()
        super.onDestroyView()
    }

}
