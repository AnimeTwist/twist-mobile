package dev.smoketrees.twist.ui.player


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.EpisodeListAdapter
import dev.smoketrees.twist.model.Result
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.fragment_episodes.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class EpisodesFragment : Fragment() {

    private val args: EpisodesFragmentArgs by navArgs()
    private val viewModel by sharedViewModel<EpisodesViewModel>()
//    private val slug = args.slugName!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episodes, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EpisodeListAdapter {
            val action = EpisodesFragmentDirections.actionEpisodesFragmentToAnimePlayerActivity(args.slugName!!, it.number!!)
            findNavController().navigate(action)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        episode_list.adapter = adapter
        episode_list.layoutManager = layoutManager


        viewModel.getAnimeDetails(args.slugName!!).observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Result.Status.LOADING -> {
                    episode_spinkit.show()
                    episode_list.hide()
                }

                Result.Status.SUCCESS -> {
                    episode_spinkit.hide()
                    episode_list.show()
                    viewModel.episodeListLiveData.postValue(it.data?.episodes)
                }

                Result.Status.ERROR -> {
                    toast(it.message!!)
                    episode_list.hide()
                }
            }
        })

        viewModel.episodeListLiveData.observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })
    }
}
