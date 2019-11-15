package dev.smoketrees.twist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.AnimeItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.animelist_item.*

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
        holder.anime_name.text = animeList[position].title
        holder.anime_name.setOnClickListener {
            listener(animeList[position])
        }
    }


    class AnimeViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    fun updateData(newData: List<AnimeItem>) {
        animeList = newData
        notifyDataSetChanged()
    }
}