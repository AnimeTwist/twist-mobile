package dev.smoketrees.twist.ui.home


import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dev.smoketrees.twist.BR
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.AnimeListAdapter
import dev.smoketrees.twist.adapters.PagedAnimeListAdapter
import dev.smoketrees.twist.databinding.FragmentHomeBinding
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.base.BaseFragment
import dev.smoketrees.twist.utils.Constants
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.toast
import org.koin.android.ext.android.inject


class HomeFragment : BaseFragment<FragmentHomeBinding, AnimeViewModel>(
    R.layout.fragment_home,
    AnimeViewModel::class,
    true
) {

    private val pref by inject<SharedPreferences>()

    override val bindingVariable = BR.homeViewModel

    private val topAiringAdapter by lazy { PagedAnimeListAdapter(requireContext()) { navigate(it) } }
    private val trendingAdapter by lazy { AnimeListAdapter(requireContext()) { navigate(it) } }
    private val topRatedAdapter by lazy { PagedAnimeListAdapter(requireContext()) { navigate(it) } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.motdBodyText.movementMethod = LinkMovementMethod.getInstance()
        dataBinding.dismissBannerButton.setOnClickListener {
            dataBinding.bannerContainer.hide()
        }

        dataBinding.topAiringRecyclerview.adapter = topAiringAdapter

        dataBinding.trendingRecyclerview.adapter = trendingAdapter

        dataBinding.topRatedRecyclerview.adapter = topRatedAdapter
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val action = HomeFragmentDirections.actionHomeFragmentToSearchActivity("")
                findNavController().navigate(action)
            }
            R.id.action_day_night -> {
                // Toggle the theme preference
                pref.edit {
                    putBoolean(
                        Constants.PreferenceKeys.IS_DAY,
                        !pref.getBoolean(Constants.PreferenceKeys.IS_DAY, true)
                    )
                }
                // Set theme
                if (pref.getBoolean(Constants.PreferenceKeys.IS_DAY, true)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
