package dev.smoketrees.twist.ui.home


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.AnimeListAdapter
import dev.smoketrees.twist.adapters.PagedAnimeListAdapter
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment() {
    private val viewModel by sharedViewModel<AnimeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        motd_body_text.movementMethod = LinkMovementMethod.getInstance()
        dismiss_banner_button.setOnClickListener {
            banner_container.hide()
        }

        val topAiringAdapter = PagedAnimeListAdapter(requireContext()) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToEpisodesFragment(
                    it.slug!!.slug!!,
                    it.id!!
                )
            findNavController().navigate(action)
        }
        val topAiringLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        top_airing_recyclerview.apply {
            adapter = topAiringAdapter
            layoutManager = topAiringLayoutManager
        }

        val trendingAdapter = AnimeListAdapter(requireContext()) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToEpisodesFragment(
                    it.slug!!.slug!!,
                    it.id!!
                )
            findNavController().navigate(action)
        }
        val trendingLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        trending_recyclerview.apply {
            adapter = trendingAdapter
            layoutManager = trendingLayoutManager
        }

        val topRatedAdapter = PagedAnimeListAdapter(requireContext()) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToEpisodesFragment(
                    it.slug!!.slug!!,
                    it.id!!
                )
            findNavController().navigate(action)
        }
        val topRatedLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        top_rated_recyclerview.apply {
            adapter = topRatedAdapter
            layoutManager = topRatedLayoutManager
        }


        // only load all anime once
        if (!viewModel.areAllLoaded) {
            viewModel.getAllAnime().observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Result.Status.LOADING -> {
                        spinkit.show()
                        top_airing_recyclerview.hide()
                        banner_container.hide()

                        top_airing_text.hide()
                        top_airing_recyclerview.hide()

                        trending_text.hide()
                        trending_recyclerview.hide()

                        top_rated_text.hide()
                        top_rated_recyclerview.hide()
                    }

                    Result.Status.SUCCESS -> {
                        spinkit.hide()
                        top_airing_recyclerview.show()
                        banner_container.show()

                        top_airing_text.show()
                        top_airing_recyclerview.show()

                        trending_text.show()
                        trending_recyclerview.show()

                        top_rated_text.show()
                        top_rated_recyclerview.show()

                        viewModel.getAllAnime().removeObservers(viewLifecycleOwner)
                        viewModel.areAllLoaded = true

                        viewModel.getTrendingAnime(40)
                            .observe(viewLifecycleOwner, Observer { trendingList ->
                                when (trendingList.status) {
                                    Result.Status.LOADING -> {
                                        spinkit.show()
                                        top_airing_recyclerview.hide()
                                        banner_container.hide()

                                        top_airing_text.hide()
                                        top_airing_recyclerview.hide()

                                        trending_text.hide()
                                        trending_recyclerview.hide()

                                        top_rated_text.hide()
                                        top_rated_recyclerview.hide()
                                    }

                                    Result.Status.SUCCESS -> {
                                        if (!trendingList.data.isNullOrEmpty()) {
                                            trendingAdapter.updateData(trendingList.data.shuffled())
                                        }
                                        spinkit.hide()
                                        top_airing_recyclerview.show()
                                        banner_container.show()

                                        top_airing_text.show()
                                        top_airing_recyclerview.show()

                                        trending_text.show()
                                        trending_recyclerview.show()

                                        top_rated_text.show()
                                        top_rated_recyclerview.show()
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
                        spinkit.show()
                        top_airing_recyclerview.hide()
                        banner_container.hide()

                        top_airing_text.hide()
                        top_airing_recyclerview.hide()

                        trending_text.hide()
                        trending_recyclerview.hide()

                        top_rated_text.hide()
                        top_rated_recyclerview.hide()
                    }

                    Result.Status.SUCCESS -> {
                        if (!trendingList.data.isNullOrEmpty()) {
                            trendingAdapter.updateData(trendingList.data)
                        }
                        spinkit.hide()
                        top_airing_recyclerview.show()
                        banner_container.show()

                        top_airing_text.show()
                        top_airing_recyclerview.show()

                        trending_text.show()
                        trending_recyclerview.show()

                        top_rated_text.show()
                        top_rated_recyclerview.show()
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
                    motd_title_text.text = it.data?.title
                    motd_body_text.setHtml(it.data?.message.toString())
                }

                Result.Status.ERROR -> {
                    toast(it.message.toString())
                }

                else -> {
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
        menu.findItem(R.id.action_search).setOnMenuItemClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchActivity("")
            findNavController().navigate(action)
            true
        }

        menu.findItem(R.id.action_account).setOnMenuItemClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAccountFragment()
            findNavController().navigate(action)
            true
        }
    }
}
