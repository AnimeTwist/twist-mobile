package dev.smoketrees.twist.adapters

import android.Manifest
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.Episode
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.episode_item.*

class EpisodeListAdapter(
    private val activity: Activity,
    private val listener: (Episode, Boolean) -> Unit
) :
    RecyclerView.Adapter<EpisodeListAdapter.EpisodeViewHolder>() {

    private var episodeList: List<Episode> = mutableListOf()
    lateinit var onBottomReachedListener: (Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EpisodeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.episode_item,
                parent,
                false
            )
        )

    override fun getItemCount() = episodeList.size

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        if (position == episodeList.size - 1) {
            onBottomReachedListener(position)
        }

        holder.episode_text.text = episodeList[position].number!!.toString()
        holder.containerView.setOnClickListener {
            listener(episodeList[position], false)
        }
        holder.download_button.setOnClickListener {
            // check for perms
            activity.runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                listener(episodeList[position], true)
            }

        }
    }


    class EpisodeViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    fun updateData(newData: List<Episode>) {
        episodeList = newData
        notifyDataSetChanged()
    }
}