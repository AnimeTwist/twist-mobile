package dev.smoketrees.twist.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.asFlow
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dev.smoketrees.twist.BR
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.SearchListAdapter
import dev.smoketrees.twist.databinding.FragmentSearchBinding
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.base.BaseFragment
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.search.WinklerWeightedRatio
import kotlinx.coroutines.CoroutineScope
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
    ),
    CoroutineScope {

    override val bindingVariable = BR.searchViewModel

    private val args: SearchFragmentArgs by navArgs()
    private var anime = emptyList<AnimeItem>()

    override val coroutineContext = Dispatchers.Main

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = SearchListAdapter {
            val action = SearchFragmentDirections.actionSearchActivityToEpisodesFragment(
                it.slug!!.slug!!,
                it.id!!
            )
            findNavController().navigate(action)
        }
        dataBinding.searchRecyclerview.adapter = adapter
        dataBinding.searchRecyclerview.layoutManager = layoutManager

        viewModel.searchResults.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                dataBinding.hasResult = true
                adapter.updateData(it)
            } else {
                dataBinding.hasResult = false
                appBar.setExpanded(true, true)
            }
            hideLoader()
        }

        viewModel.getAllAnime().observe(viewLifecycleOwner) {
            when (it.status) {
                Result.Status.SUCCESS -> anime = it.data ?: emptyList()
                Result.Status.ERROR -> anime = emptyList()
                else -> {
                }
            }
        }

        launch(Dispatchers.IO) {
            viewModel.searchQuery.asFlow().collect { searchString ->
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = (menu.findItem(R.id.action_search).actionView as SearchView)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                viewModel.searchQuery.postValue(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchQuery.postValue(query)
                return false
            }
        })
        if (args.query.isBlank()) {
            menu.findItem(R.id.action_search).expandActionView()
        } else {
            searchView.setQuery(args.query, true)
        }
    }
}
