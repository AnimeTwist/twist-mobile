package dev.smoketrees.twist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.AnimeItem
import kotlinx.android.synthetic.main.animelist_item.view.*

class AnimeListAdapter(private val listener: (AnimeItem) -> Unit) :
    RecyclerView.Adapter<AnimeListAdapter.AnimeViewHolder>() {

    private var animeList: List<AnimeItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AnimeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.animelist_item,
                parent,
                false
            )
        )

    override fun getItemCount() = animeList.size

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.animeTitle.text = animeList[position].title
        holder.animeTitle.setOnClickListener {
            listener(animeList[position])
        }
    }


    class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeTitle: TextView = itemView.anime_name
    }

    fun updateData(newData: List<AnimeItem>) {
        animeList = newData
        notifyDataSetChanged()
    }
}