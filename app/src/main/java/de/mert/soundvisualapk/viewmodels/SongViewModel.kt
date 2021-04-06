package de.mert.soundvisualapk.viewmodels

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.mert.soundvisualapk.activities.ConnectActivity
import de.mert.soundvisualapk.fragments.SongPlayer
import de.mert.soundvisualapk.network.GetSong
import de.mert.soundvisualapk.network.GetSongs
import de.mert.soundvisualapk.network.SongApi
import kotlinx.coroutines.launch
import java.lang.Exception

class SongViewModel : ViewModel() {
    private val songs: MutableLiveData<List<GetSongs>> = MutableLiveData()
    private val song: MutableLiveData<GetSong> = MutableLiveData()

    companion object {
        const val ERROR_MESSAGE = "ErrorMessage"
    }

    fun getSongs(): LiveData<List<GetSongs>> {
        return songs
    }

    fun getSong(): LiveData<GetSong> {
        loadSong()
        return song
    }

    fun loadSongs(view: View, path: String) {
        viewModelScope.launch {
            try {
                songs.value = SongApi.retrofitService.getSongs(ConnectActivity.baseUrl + "/getSongs?path=" + path)
            } catch (e: Exception) {
              /*  val intent = Intent(view.context, ConnectActivity::class.java).apply {
                    putExtra(ERROR_MESSAGE, e.message)
                } */

                e.printStackTrace()

            //    view.context.startActivity(intent)
            }
        }
    }

    private fun loadSong() {
        viewModelScope.launch {
            try {
                song.value = SongApi.retrofitService.getSong(ConnectActivity.baseUrl + "/getSong")
                SongPlayer.errorMessage = ""
            } catch (e: Exception) {
                SongPlayer.errorMessage = "No Connection"
            }
        }
    }

    fun stopSong() {
        viewModelScope.launch {
            SongApi.retrofitService.stopSong(ConnectActivity.baseUrl + "/stopSong")
        }
    }
}