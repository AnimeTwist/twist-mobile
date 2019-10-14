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
import dev.smoketrees.twist.model.Result
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import dev.smoketrees.twist.utils.toast
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {

    private lateinit var exoPlayer: ExoPlayer

    private val viewModel by sharedViewModel<AnimeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        exoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext())
//        player_view.player = exoPlayer
//
//        val animeName = "toaru-majutsu-no-index"
//
//        viewModel.getAnimeSources(animeName).observe(this, Observer {
//            when (it.status) {
//                Result.Status.LOADING -> {
//                    toast("Loading")
//                }
//
//                Result.Status.SUCCESS -> {
//                    val sourceFactory = DefaultHttpDataSourceFactory(
//                        Util.getUserAgent(
//                            requireContext(),
//                            "twist.moe"
//                        )
//                    )
//
//                    val mediaSource = ProgressiveMediaSource.Factory {
//                        val dataSource = sourceFactory.createDataSource()
//                        dataSource.setRequestProperty("Referer", "https://twist.moe/a/$animeName/0")
//                        dataSource
//                    }
//                        .createMediaSource(
//                            Uri.parse(
//                                "https://twist.moe${CryptoHelper.decryptSourceUrl(
//                                    requireContext(),
//                                    it?.data?.get(0)?.source!!
//                                )}"
//                            )
//                        )
//
//                    exoPlayer.prepare(mediaSource)
//                }
//
//                Result.Status.ERROR -> {
//                    toast(it.message!!)
//                }
//            }
//        })

        val adapter = AnimeListAdapter {
            val action = HomeFragmentDirections.actionHomeFragmentToEpisodesFragment(it.slug!!.slug!!)
            findNavController().navigate(action)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        anime_list.adapter = adapter
        anime_list.layoutManager = layoutManager

        viewModel.getAllAnime().observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Result.Status.LOADING -> {
                    spinkit.show()
                    anime_list.hide()
                }

                Result.Status.SUCCESS -> {
                    spinkit.hide()
                    anime_list.show()
                    viewModel.animeListLiveData.postValue(it.data!!)

                }

                Result.Status.ERROR -> {
                    toast(it.message!!)
                    anime_list.hide()
                }
            }
        })

        viewModel.animeListLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {

            } else {
                adapter.updateData(it)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
