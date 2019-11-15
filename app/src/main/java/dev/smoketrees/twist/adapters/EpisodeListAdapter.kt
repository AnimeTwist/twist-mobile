package dev.smoketrees.twist.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.Episode
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.animelist_item.*

class EpisodeListAdapter(private val listener: (Episode) -> Unit) :
    RecyclerView.Adapter<EpisodeListAdapter.EpisodeViewHolder>() {

    private var episodeList: List<Episode> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EpisodeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.animelist_item,
                parent,
                false
            )
        )

    override fun getItemCount() = episodeList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.anime_name.text = "Episode no ${episodeList[position].number!!}"
        holder.anime_name.setOnClickListener {
            listener(episodeList[position])
        }
    }


    class EpisodeViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    fun updateData(newData: List<Episode>) {
        episodeList = newData
        notifyDataSetChanged()
    }
}