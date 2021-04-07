package de.mert.soundvisualapk.activities.recycleviewadapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.mert.soundvisualapk.R
import de.mert.soundvisualapk.activities.ConnectActivity
import de.mert.soundvisualapk.fragments.SongPlayer
import de.mert.soundvisualapk.network.GetSongs
import de.mert.soundvisualapk.network.PlaySong
import de.mert.soundvisualapk.network.SongApi
import de.mert.soundvisualapk.viewmodels.SongViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Exception

class SongsRecycleAdapter(private val dataSet: List<GetSongs>) :
    RecyclerView.Adapter<SongsRecycleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item, parent, false)

        return ViewHolder(view)
    }

    /**
     * Sets the name and the click listener of the items
     * if its a dir the click listener will update the songs with the songs in the given path
     * if its a song it will send a post request to the server to play the song
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view: View = holder.itemView
        holder.textView.text = dataSet[position].name

        if (dataSet[position].type.equals("dir")) {
            holder.textView.text = holder.textView.text.toString() + "  (dir)"
            holder.textView.setOnClickListener {
                SongPlayer.updateSongs(dataSet[position].path)
            }
        } else
            holder.textView.setOnClickListener { playSong(dataSet[position].path, view) }

    }

    /**
     * The actual function who sends the post request to play a song
     */
    private fun playSong(name: String, view: View) {
        MainScope().launch {
            try {
                val body = PlaySong(
                    name,
                    ""
                )

                SongApi.retrofitService.playSong(ConnectActivity.baseUrl + "/playSong", body)
            } catch (e: Exception) {
                val intent = Intent(
                        view.context, ConnectActivity::class.java).apply {
                    putExtra(SongViewModel.ERROR_MESSAGE, "Connection Failed")
                }

                view.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size
}

