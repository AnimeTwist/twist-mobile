package dev.smoketrees.twist.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.SearchListAdapter
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.search.WinklerWeightedRatio
import dev.smoketrees.twist.utils.show
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.ToStringFunction
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SearchFragment : Fragment(), CoroutineScope {
    private val args: SearchFragmentArgs by navArgs()
    private val viewModel: AnimeViewModel by sharedViewModel()
    private var anime = emptyList<AnimeItem>()

    override val coroutineContext = Dispatchers.Main

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_search, container, false)
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
        search_recyclerview.adapter = adapter
        search_recyclerview.layoutManager = layoutManager

        viewModel.searchResults.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                search_recyclerview.show()
                no_results_text.hide()
                spinkit.hide()
                adapter.updateData(it)
            } else {
                search_recyclerview.hide()
                spinkit.hide()
                no_results_text.show()
            }
        }

        viewModel.getAllAnime().observe(viewLifecycleOwner) {
            when (it.status) {
                Result.Status.SUCCESS -> anime = it.data ?: emptyList()
                Result.Status.ERROR -> anime = emptyList()
                else -> {}
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
