package dev.smoketrees.twist.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.SearchListAdapter
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SearchFragment : Fragment() {

    private val args: SearchFragmentArgs by navArgs()
    private val viewModel: AnimeViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = SearchListAdapter {
            val action = SearchFragmentDirections.actionSearchActivityToEpisodesFragment(it.slug!!.slug!!, it.id!!)
            findNavController().navigate(action)
        }
        search_recyclerview.adapter = adapter
        search_recyclerview.layoutManager = layoutManager

        viewModel.searchAnime(args.query).observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                search_recyclerview.show()
                no_results_text.hide()
                adapter.updateData(it)
            } else {
                search_recyclerview.hide()
                no_results_text.show()
            }
        })

    }
}
