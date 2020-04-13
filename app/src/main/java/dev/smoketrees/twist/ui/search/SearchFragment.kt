package dev.smoketrees.twist.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dev.smoketrees.twist.BR
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.SearchListAdapter
import dev.smoketrees.twist.databinding.FragmentSearchBinding
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.base.BaseFragment
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.search.WinklerWeightedRatio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction

class SearchFragment :
    BaseFragment<FragmentSearchBinding, AnimeViewModel>(
        R.layout.fragment_search,
        AnimeViewModel::class,
        true
    ) {

    override val bindingVariable = BR.searchViewModel

    private val args: SearchFragmentArgs by navArgs()
    private var anime = emptyList<AnimeItem>()

    private var menuCreated = false

    private val adapter by lazy {
        SearchListAdapter {
            val action = SearchFragmentDirections.actionSearchActivityToEpisodesFragment(
                it.slug!!.slug!!,
                it.id!!
            )
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("onCreateView", "EXECUTED")
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.e("onActivityCreated", "EXECUTED")
        super.onActivityCreated(savedInstanceState)

        viewModel.searchResults.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                dataBinding.hasResult = true
                adapter.updateData(it)
            } else {
                dataBinding.hasResult = false
                appBar.setExpanded(true, true)
            }
            Log.e("Updated shown data", it.size.toString() + " " + menuCreated + " " + viewModel.searchQuery.value)
            hideLoader()
        }

        viewModel.allAnimeLivedata.observe(viewLifecycleOwner) {
            when (it.status) {
                Result.Status.SUCCESS -> anime = it.data ?: emptyList()
                Result.Status.ERROR -> anime = emptyList()
                else -> {
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("onViewCreated", "EXECUTED")

        dataBinding.searchRecyclerview.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.searchQuery.asFlow().collect { searchString ->
                if (anime.count() != 0) {
                    viewModel.searchResults.postValue((FuzzySearch.extractAll(
                        searchString,
                        anime,
                        ToStringFunction { it.title },
                        WinklerWeightedRatio(),
                        65
                    ) + FuzzySearch.extractAll(
                        searchString,
                        anime,
                        ToStringFunction { it.altTitle },
                        WinklerWeightedRatio(),
                        65
                    )).sortedByDescending { it.score }.map { it.referent })
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.e("onCreateOptionsMenu", "EXECUTED")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = (menu.findItem(R.id.action_search).actionView as SearchView)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.maxWidth = Int.MAX_VALUE

        // Handle query changes
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                if (menuCreated) {
                    viewModel.searchQuery.postValue(query)
                    Log.e("onQueryTextChange", query)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (menuCreated) {
                    viewModel.searchQuery.postValue(query)
                    Log.e("onQueryTextSubmit", query)
                }
                return false
            }
        })

        // Go back if not expanded
        menu.findItem(R.id.action_search).setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                if (!args.query.isBlank())
                    searchView.post { searchView.setQuery(args.query, false) }
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                findNavController().popBackStack()
                return false
            }
        })

        // Expand
        menu.findItem(R.id.action_search).expandActionView()

        // Preserve search query on configuration change
        if (viewModel.searchQuery.value != null) {
            searchView.post {
                searchView.setQuery(viewModel.searchQuery.value, false)
                menuCreated = true
            }
        } else {
            menuCreated = true
        }
    }
}
