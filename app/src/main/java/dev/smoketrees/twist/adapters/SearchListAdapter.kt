package dev.smoketrees.twist.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.utils.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.anime_search_item.*

class SearchListAdapter(val listener: (AnimeItem) -> Unit) :
    RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>() {

    private var animeList: List<AnimeItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.anime_search_item,
                parent,
                false
            )
        )

    override fun getItemCount() = animeList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = animeList[position]
        holder.anime_name.text = item.title
        holder.anime_alt_title.text = item.altTitle
        if (item.ongoing == 1) {
            holder.anime_ongoing.show()
        }
        holder.containerView.setOnClickListener {
            listener(animeList[position])
        }
    }


    class SearchViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    fun updateData(newData: List<AnimeItem>) {
        animeList = newData
        notifyDataSetChanged()
    }
}
