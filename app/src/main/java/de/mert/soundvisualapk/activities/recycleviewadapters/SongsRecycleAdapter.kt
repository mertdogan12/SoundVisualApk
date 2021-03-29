package de.mert.soundvisualapk.activities.recycleviewadapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.mert.soundvisualapk.R
import de.mert.soundvisualapk.activities.ConnectActivity
import de.mert.soundvisualapk.network.PlaySong
import de.mert.soundvisualapk.network.SongApi
import de.mert.soundvisualapk.viewmodels.SongViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

class SongsRecycleAdapter(private val dataSet: List<String>) : RecyclerView.Adapter<SongsRecycleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view: View = holder.itemView
        holder.textView.text = dataSet[position]

        holder.textView.setOnClickListener{
            MainScope().launch {
                try {
                    val body = PlaySong(
                        "mk.mp3",
                        "https://i.pinimg.com/originals/5a/7f/8d/5a7f8d2ce48da3b3e7a375c2d58e3255.gif"
                    )

                    SongApi.retrofitService.playSong(ConnectActivity.baseUrl + "/playSong", body)
                } catch (e: Exception) {
                    val intent = Intent(view.context, ConnectActivity::class.java).apply {
                        putExtra(SongViewModel.ERROR_MESSAGE, "Connection Failed")
                    }

                    view.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size
}

