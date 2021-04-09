package de.mert.soundvisualapk.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import de.mert.soundvisualapk.databinding.ActivityConnectBinding
import de.mert.soundvisualapk.viewmodels.SongViewModel

class ConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectBinding

    companion object {
        var baseUrl: String = "http://192.168.178.29:3000"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Listener
        binding.backGround.setOnClickListener { closeKeyboard(it) }
        binding.ipAddressInput.setOnKeyListener { v, keyCode, _ -> handleKeyEvent(v, keyCode) }
        binding.connect.setOnClickListener { connect(it) }

        // Show the error message if one is given
        if (!intent.getStringExtra(SongViewModel.ERROR_MESSAGE).isNullOrEmpty())
            binding.errorMessage.text = baseUrl + " --> " + intent.getStringExtra(SongViewModel.ERROR_MESSAGE)
    }

    /**
     * Just close the keyboard if enter
     */
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            closeKeyboard(view)
            return true
        }

        return false
    }

    /**
     * The actual function with close the keyboard
     */
    private fun closeKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Sets the ip and starts the MainActivity with then starts the connection
     */
    private fun connect(view: View) {
        val intent = Intent(view.context, MainActivity::class.java)
        val text = binding.ipAddressInput.text.toString().replace(" ", "")

        if (text.isNotBlank())
            baseUrl = (if (binding.httpSwitch.isChecked) "http://" else "https://") + text

        view.context.startActivity(intent)
    }
}