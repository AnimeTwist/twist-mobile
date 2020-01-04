package dev.smoketrees.twist.ui.home


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
        val autofitLayoutManager =
            GridAutofitLayoutManager(
                requireContext(), TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 100f,
                    requireContext().resources.displayMetrics
                ).toInt()
            )
        anime_list.apply {
            adapter = animeListAdapter
            layoutManager = autofitLayoutManager
            smoothScrollToPosition(0)
        }

        viewModel.animePagedList.observe(viewLifecycleOwner, Observer {
            animeListAdapter.submitList(it)
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
