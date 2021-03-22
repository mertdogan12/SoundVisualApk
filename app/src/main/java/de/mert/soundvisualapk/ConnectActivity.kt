package de.mert.soundvisualapk

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import de.mert.soundvisualapk.databinding.ActivityConnectBinding
import de.mert.soundvisualapk.network.SongApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class ConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectBinding

    companion object {
        var BASE_URL = "http://localhost:3000"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backGround.setOnClickListener { closeKeyboard(it) }

        binding.ipAddressInput.setOnKeyListener { v, keyCode, _ -> handleKeyEvent(v, keyCode) }

        binding.connect.setOnClickListener { connect(it) }
    }

    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            closeKeyboard(view)
            return true
        }

        return false
    }

    private fun closeKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun connect(view: View) {
        val context = view.context
        var songs: String? = null
        var pictures: String? = null

        runBlocking {
            launch {
                try {
                    songs = SongApi.retrofitService.getSongs()
                    pictures = "Picture api not available"
                } catch (e: Exception) {
                    songs = e.message
                    pictures = e.message
                }

            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("value", songs)
        }
        context.startActivity(intent)
    }
}