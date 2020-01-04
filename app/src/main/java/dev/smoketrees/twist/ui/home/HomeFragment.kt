package dev.smoketrees.twist.ui.home


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.AnimeListAdapter
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.*
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
        header_body.movementMethod = LinkMovementMethod.getInstance()

        dismiss_banner_button.setOnClickListener {
            banner_container.hide()
        }

        val animeListAdapter = AnimeListAdapter(viewModel, requireContext()) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToEpisodesFragment(
                    it.slug!!.slug!!,
                    it.id!!
                )
            findNavController().navigate(action)
        }
        val animeListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        top_airing_recyclerview.apply {
            adapter = animeListAdapter
            layoutManager = animeListLayoutManager
            smoothScrollToPosition(0)
        }

        viewModel.animePagedList.observe(viewLifecycleOwner, Observer {
            animeListAdapter.submitList(it)
        })

        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.LOADING -> {
                    spinkit.show()
                    top_airing_recyclerview.hide()
                    banner_container.hide()

                    top_airing_text.hide()
                    top_airing_recyclerview.hide()

                    trending_text.hide()
                    trending_recyclerview.hide()
                }

                Result.Status.SUCCESS -> {
                    spinkit.hide()
                    top_airing_recyclerview.show()
                    banner_container.show()

                    top_airing_text.show()
                    top_airing_recyclerview.show()

                    trending_text.show()
                    trending_recyclerview.show()
                    viewModel.networkState.removeObservers(viewLifecycleOwner)
                }

                else -> {
                    toast(it.message.toString())
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = (menu.findItem(R.id.action_search).actionView as SearchView)
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val action = HomeFragmentDirections.actionHomeFragmentToSearchActivity("%${query}%")
                findNavController().navigate(action)
                return true
            }
        })
    }
}
