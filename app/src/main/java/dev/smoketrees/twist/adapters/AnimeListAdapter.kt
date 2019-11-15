package dev.smoketrees.twist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.AnimeItem
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.toast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.animelist_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AnimeListAdapter(
    private val viewModel: AnimeViewModel,
    private val context: Context,
    val listener: (AnimeItem) -> Unit
) :
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
        holder.containerView.setOnClickListener {
            listener(animeList[position])
        }
        if (animeList[position].imgUrl == null || animeList[position].imgUrl == "") {
            Glide.with(context).clear(holder.anime_image)
            holder.anime_image.setImageDrawable(null)
            context.toast(animeList[position].toString())
            viewModel.getAnimeImageUrl(animeList[position])
        } else {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            Glide.with(context).load(animeList[position].imgUrl)
                .placeholder(circularProgressDrawable).into(holder.anime_image)
        }
    }


    class AnimeViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    fun updateData(newData: List<AnimeItem>) {
        animeList = newData
        notifyDataSetChanged()
    }
}