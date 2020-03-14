package dev.smoketrees.twist.ui.home


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.smoketrees.twist.BR
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.AnimeListAdapter
import dev.smoketrees.twist.adapters.PagedAnimeListAdapter
import dev.smoketrees.twist.databinding.FragmentHomeBinding
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.base.BaseFragment
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.toast


class HomeFragment : BaseFragment<FragmentHomeBinding, AnimeViewModel>(
    R.layout.fragment_home,
    AnimeViewModel::class,
    true
) {

    override val bindingVariable = BR.homeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dataBinding.motdBodyText.movementMethod = LinkMovementMethod.getInstance()
        dataBinding.dismissBannerButton.setOnClickListener {
            dataBinding.bannerContainer.hide()
        }

        val topAiringAdapter = PagedAnimeListAdapter(requireContext()) { navigate(it) }
        val topAiringLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dataBinding.topAiringRecyclerview.apply {
            adapter = topAiringAdapter
            layoutManager = topAiringLayoutManager
        }

        val trendingAdapter = AnimeListAdapter(requireContext()) { navigate(it) }
        val trendingLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dataBinding.trendingRecyclerview.apply {
            adapter = trendingAdapter
            layoutManager = trendingLayoutManager
        }

        val topRatedAdapter = PagedAnimeListAdapter(requireContext()) { navigate(it) }
        val topRatedLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dataBinding.topRatedRecyclerview.apply {
            adapter = topRatedAdapter
            layoutManager = topRatedLayoutManager
        }


        // only load all anime once
        if (!viewModel.areAllLoaded) {
            viewModel.getAllAnime().observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Result.Status.LOADING -> {
                        showLoader()
                        dataBinding.hasResult = false
                    }

                    Result.Status.SUCCESS -> {
                        hideLoader()
                        dataBinding.hasResult = true

                        viewModel.getAllAnime().removeObservers(viewLifecycleOwner)
                        viewModel.areAllLoaded = true

                        viewModel.getTrendingAnime(40)
                            .observe(viewLifecycleOwner, Observer { trendingList ->
                                when (trendingList.status) {
                                    Result.Status.LOADING -> {
                                        showLoader()
                                        dataBinding.hasResult = false
                                    }

                                    Result.Status.SUCCESS -> {
                                        if (!trendingList.data.isNullOrEmpty()) {
                                            trendingAdapter.updateData(trendingList.data.shuffled())
                                        }
                                        hideLoader()
                                        dataBinding.hasResult = true
                                    }

                                    Result.Status.ERROR -> {
                                        toast(trendingList.message.toString())
                                    }
                                }
                            })
                        viewModel.topAiringAnime.observe(viewLifecycleOwner, Observer { pagedList ->
                            topAiringAdapter.submitList(pagedList)
                        })
                        viewModel.topRatedAnime.observe(viewLifecycleOwner, Observer { pagedList ->
                            topRatedAdapter.submitList(pagedList)
                        })
                    }

                    Result.Status.ERROR -> {
                        toast(it.message.toString())
                    }
                }
            })
        } else {
            viewModel.getTrendingAnime(40).observe(viewLifecycleOwner, Observer { trendingList ->
                when (trendingList.status) {
                    Result.Status.LOADING -> {
                        showLoader()
                        dataBinding.hasResult = false
                    }

                    Result.Status.SUCCESS -> {
                        if (!trendingList.data.isNullOrEmpty()) {
                            trendingAdapter.updateData(trendingList.data)
                        }
                        hideLoader()
                        dataBinding.hasResult = true
                    }

                    Result.Status.ERROR -> {
                        toast(trendingList.message.toString())
                    }
                }
            })
            viewModel.topAiringAnime.observe(viewLifecycleOwner, Observer { pagedList ->
                topAiringAdapter.submitList(pagedList)
            })
            viewModel.topRatedAnime.observe(viewLifecycleOwner, Observer { pagedList ->
                topRatedAdapter.submitList(pagedList)
            })
        }

        viewModel.getMotd().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.SUCCESS -> {
                    dataBinding.motdTitleText.text = it.data?.title
                    dataBinding.motdBodyText.setHtml(it.data?.message.toString())
                }

                Result.Status.ERROR -> {
                    toast(it.message.toString())
                }

                else -> {
                }
            }
        })

    }

    private fun navigate(anime: AnimeItem) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToEpisodesFragment(
                anime.slug!!.slug!!,
                anime.id!!
            )
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
        menu.findItem(R.id.action_search).setOnMenuItemClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchActivity("")
            findNavController().navigate(action)
            true
        }
    }
}
