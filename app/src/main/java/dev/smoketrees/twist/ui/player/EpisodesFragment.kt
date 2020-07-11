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
import dev.smoketrees.twist.ui.home.MainActivity
import dev.smoketrees.twist.utils.hide
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

    private val episodeAdapter by lazy {
        EpisodeListAdapter(requireActivity()) { ep, _ ->
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

        viewModel.getAnimeDetails(args.slugName, args.id).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.LOADING -> {
                    showLoader()
                    dataBinding.hasResult = false
                    fab.hide()
                }

                Result.Status.SUCCESS -> {
                    it.data?.let { detailsEntity ->
                        dataBinding.anime = detailsEntity

/*                        detailsEntity.airing?.let { ongoing ->
                            if (ongoing) dataBinding.animeOngoingText.show() else dataBinding.animeOngoingText.hide()
                        }*/
                        Glide.with(requireContext())
                            .load(detailsEntity.imageUrl)
                            .into(dataBinding.animeImage)

                        hideLoader()
                        dataBinding.hasResult = true

                        if (detailsEntity.episodeList.isNotEmpty()) {
                            episodeAdapter.updateData(it.data.episodeList)
                        }

                        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < episodeAdapter.itemCount - 1) {
                            fab.show()
                        }
                    }
                }

                Result.Status.ERROR -> {
                    toast(it.message!!.msg)
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
