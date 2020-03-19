package dev.smoketrees.twist.adapters


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.AnimeItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.animelist_item.*

class AnimeListAdapter(
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
        if (animeList[position].nejireExtension?.poster_image == null || animeList[position].nejireExtension?.poster_image == "") {
            Glide.with(context).clear(holder.anime_image)
            holder.anime_image.setImageDrawable(null)
        } else {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.apply {
                setColorSchemeColors(Color.rgb(105, 240, 174))
                strokeWidth = 10f
                centerRadius = 40f
                start()
            }
            Glide.with(context).load(animeList[position].nejireExtension?.poster_image)
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