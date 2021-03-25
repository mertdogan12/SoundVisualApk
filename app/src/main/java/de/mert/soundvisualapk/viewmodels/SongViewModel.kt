package de.mert.soundvisualapk.viewmodels

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.mert.soundvisualapk.activities.ConnectActivity
import de.mert.soundvisualapk.network.SongApi
import kotlinx.coroutines.launch
import java.lang.Exception

class SongViewModel : ViewModel() {
    private val songs: MutableLiveData<List<String>> = MutableLiveData()

    companion object {
        const val ERROR_MESSAGE = "ErrorMessage"
    }

    fun getSongs(view: View): LiveData<List<String>> {
        return songs
    }

    fun loadSongs(view: View) {
        viewModelScope.launch {
            try {
                songs.value = SongApi.retrofitService.getSongs(ConnectActivity.baseUrl)
            } catch (e: Exception) {
                val intent = Intent(view.context, ConnectActivity::class.java).apply {
                    putExtra(ERROR_MESSAGE, "Connection Failed")
                }

                view.context.startActivity(intent)
            }
        }
    }
}

