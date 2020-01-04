package dev.smoketrees.twist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
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
    private val listener: (AnimeItem) -> Unit
) :
    PagedListAdapter<AnimeItem, AnimeListAdapter.AnimeViewHolder>(AnimeItem.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AnimeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.animelist_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val item = getItem(position)

        holder.anime_name.text = item?.title
        holder.containerView.setOnClickListener {
            item?.let(listener)
        }
        if (item?.nejireExtension?.poster_image == null || item.nejireExtension?.poster_image == "") {
            Glide.with(context).clear(holder.anime_image)
            holder.anime_image.setImageDrawable(null)
        } else {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 7f
            circularProgressDrawable.centerRadius = 40f
            circularProgressDrawable.start()
            Glide.with(context).load(item.nejireExtension?.poster_image)
                .placeholder(circularProgressDrawable).into(holder.anime_image)
        }
    }


    class AnimeViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer
}