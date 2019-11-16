package dev.smoketrees.twist.ui.home


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.SimpleCursorAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.AnimeListAdapter
import dev.smoketrees.twist.db.AnimeDao
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {


    private val viewModel by sharedViewModel<AnimeViewModel>()
    private val animeDao: AnimeDao by inject()
    private lateinit var searchAdapter: SimpleCursorAdapter

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
        viewModel.lifeCycleOwner = viewLifecycleOwner
        header_body.movementMethod = LinkMovementMethod.getInstance()

        val adapter = AnimeListAdapter(viewModel, requireContext()) {
            val action =
                HomeFragmentDirections.actionHomeFragmentToEpisodesFragment(
                    it.slug!!.slug!!,
                    it.id!!
                )
            findNavController().navigate(action)
        }
        val layoutManager =
            GridLayoutManager(requireContext(), 4)

        anime_list.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            smoothScrollToPosition(0)
        }

        viewModel.getAllAnime().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.LOADING -> {
                    spinkit.show()
                    anime_list.hide()
                }

                Result.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        adapter.updateData(it.data)
                        spinkit.hide()
                        anime_list.show()
                    }
                }

                Result.Status.ERROR -> {
                    toast(it.message!!)
                    anime_list.hide()
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
