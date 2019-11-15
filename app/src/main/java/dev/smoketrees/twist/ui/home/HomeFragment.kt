package dev.smoketrees.twist.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
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

    private lateinit var exoPlayer: ExoPlayer

    private val viewModel by sharedViewModel<AnimeViewModel>()
    private val animeDao: AnimeDao by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.lifeCycleOwner = viewLifecycleOwner

        val adapter = AnimeListAdapter(viewModel, requireContext()) {
            val action = HomeFragmentDirections.actionHomeFragmentToEpisodesFragment(it.slug!!.slug!!)
            findNavController().navigate(action)
        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        anime_list.adapter = adapter
        anime_list.layoutManager = layoutManager
        anime_list.smoothScrollToPosition(0)

        viewModel.getAllAnime().observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Result.Status.LOADING -> {
                    spinkit.show()
                    anime_list.hide()
                }

                Result.Status.SUCCESS -> {
//                    viewModel.getAnimeImageUrls(it.data!!)
                    spinkit.hide()
                    anime_list.show()
                }

                Result.Status.ERROR -> {
                    toast(it.message!!)
                    anime_list.hide()
                }
            }
        })

        animeDao.getAllAnime().observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                adapter.updateData(it)
            }
        })
    }
}
