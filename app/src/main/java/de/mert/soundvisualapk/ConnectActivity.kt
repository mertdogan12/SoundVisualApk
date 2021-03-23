package de.mert.soundvisualapk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import de.mert.soundvisualapk.databinding.ActivityConnectBinding
import de.mert.soundvisualapk.network.SongApi
import de.mert.soundvisualapk.viewmodels.SongViewModel
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private var baseUrl: String = SongApi.BASE_URL

class ConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backGround.setOnClickListener { closeKeyboard(it) }

        binding.ipAddressInput.setOnKeyListener { v, keyCode, _ -> handleKeyEvent(v, keyCode) }

        binding.connect.setOnClickListener { connect(it) }

        if (!intent.getStringExtra(SongViewModel.ERROR_MESSAGE).isNullOrEmpty())
            binding.errorMessage.text = baseUrl + " --> " + intent.getStringExtra(SongViewModel.ERROR_MESSAGE)
    }

    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            connect(view)
            return true
        }

        return false
    }

    private fun closeKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun connect(view: View) {
        val intent = Intent(view.context, MainActivity::class.java)
        baseUrl = binding.ipAddressInput.text.toString()

        if (baseUrl.isNotBlank())
            baseUrl = (if (binding.httpSwitch.isChecked) "http://" else "https://") + baseUrl

        SongApi.retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(baseUrl)
            .build()

        view.context.startActivity(intent)
    }
}