package dev.smoketrees.twist.ui.player


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dev.smoketrees.twist.R
import dev.smoketrees.twist.adapters.EpisodeListAdapter
import dev.smoketrees.twist.model.twist.Result
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.fragment_episodes.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class EpisodesFragment : Fragment() {

    private val args: EpisodesFragmentArgs by navArgs()
    private val viewModel by sharedViewModel<EpisodesViewModel>()
    private val animeViewModel by sharedViewModel<AnimeViewModel>()
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
            val action = EpisodesFragmentDirections.actionEpisodesFragmentToAnimePlayerActivity(
                args.slugName!!,
                it.number!!
            )
            findNavController().navigate(action)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        episode_list.adapter = adapter
        episode_list.layoutManager = layoutManager


        viewModel.getAnimeDetails(args.slugName!!).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Result.Status.LOADING -> {
                    episode_spinkit.show()
                    episode_list.hide()
                    anime_image.hide()
                    anime_title.hide()
                    anime_rating.hide()
                    anime_episodes.hide()
                    anime_ongoing_text.hide()
                }

                Result.Status.SUCCESS -> {
//                    animeViewModel.getAnimeImageUrl(it.data?.id!!)

                    anime_title.text = it.data?.title
                    anime_episodes.text = "${it.data?.episodes?.size} episodes"
                    animeViewModel.getMALAnime(it.data?.slug?.slug!!)
                        .observe(viewLifecycleOwner, Observer { jikanResult ->
                            when (jikanResult.status) {
                                Result.Status.SUCCESS -> {
                                    anime_rating.text =
                                        "Score: ${jikanResult.data?.results?.get(0)?.score}/10"
                                    animeViewModel.getMALAnimeById(jikanResult.data?.results?.get(0)?.malId!!)
                                        .observe(viewLifecycleOwner, Observer { malAnime ->
                                            anime_studio.text = malAnime.data?.studios?.get(0)?.name
                                                ?: "Unknown Studio"
                                            malAnime.data?.airing?.let {ongoing ->
                                                if (ongoing) anime_ongoing_text.show() else anime_ongoing_text.hide()
                                            }
                                            episode_spinkit.hide()
                                            episode_list.show()
                                            anime_image.show()
                                            anime_title.show()
                                            anime_rating.show()
                                            anime_episodes.show()
                                        })
                                    Glide.with(requireContext())
                                        .load(jikanResult.data?.results?.get(0)?.imageUrl)
                                        .into(anime_image)
                                    episode_spinkit.hide()
                                    episode_list.show()
                                    anime_image.show()
                                    anime_title.show()
                                    anime_rating.show()
                                    anime_episodes.show()
                                }
                            }
                        })

                    viewModel.episodeListLiveData.postValue(it.data.episodes)
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
