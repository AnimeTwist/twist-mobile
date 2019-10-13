package dev.smoketrees.twist.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.Result
import dev.smoketrees.twist.ui.AnimeViewModel
import dev.smoketrees.twist.utils.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {

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

        viewModel.getAnimeDetails("toaru-majutsu-no-index").observe(this, Observer {
            when (it.status) {
                Result.Status.LOADING -> {
                    toast("Loading")
                }

                Result.Status.SUCCESS -> {
                    toast(it.data.toString())
                }

                Result.Status.ERROR -> {
                    toast(it.message!!)
                }
            }
        })
    }
}
