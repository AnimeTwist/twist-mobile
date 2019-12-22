package dev.smoketrees.twist.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import dev.smoketrees.twist.R
import dev.smoketrees.twist.model.twist.Episode
import dev.smoketrees.twist.ui.home.AnimeViewModel
import dev.smoketrees.twist.utils.longToast
import dev.smoketrees.twist.utils.toast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.animelist_item.*
import kotlinx.android.synthetic.main.episode_item.*

class EpisodeListAdapter(
    private val activity: Activity,
    private val listener: (Episode, Boolean) -> Unit
) :
    RecyclerView.Adapter<EpisodeListAdapter.EpisodeViewHolder>() {

    private var episodeList: List<Episode> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EpisodeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.episode_item,
                parent,
                false
            )
        )

    override fun getItemCount() = episodeList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.episode_text.text = "Episode no ${episodeList[position].number!!}"
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