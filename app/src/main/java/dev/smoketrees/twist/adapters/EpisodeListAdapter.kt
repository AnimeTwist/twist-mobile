package dev.smoketrees.twist.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.Episode
import kotlinx.android.synthetic.main.animelist_item.view.*

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
        holder.animeTitle.text = "Episode no ${episodeList[position].number!!}"
        holder.animeTitle.setOnClickListener {
            listener(episodeList[position])
        }
    }


    class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeTitle: TextView = itemView.anime_name
    }

    fun updateData(newData: List<Episode>) {
        episodeList = newData
        notifyDataSetChanged()
    }
}