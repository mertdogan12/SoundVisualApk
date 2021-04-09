package de.mert.soundvisualapk.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import de.mert.soundvisualapk.R
import androidx.recyclerview.widget.LinearLayoutManager
import de.mert.soundvisualapk.activities.ConnectActivity
import de.mert.soundvisualapk.recycleviewadapters.SongsRecycleAdapter
import de.mert.soundvisualapk.databinding.FragmentSongPlayerBinding
import de.mert.soundvisualapk.network.GetSongs
import de.mert.soundvisualapk.network.PlaySong
import de.mert.soundvisualapk.network.SongApi
import de.mert.soundvisualapk.viewmodels.SongViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var _binding: FragmentSongPlayerBinding? = null
private val binding get() = _binding!!
private val handler: Handler = Handler()
private var _api: SongViewModel? = null
private val api get() = _api!!
private var _liveCycleOwner: LifecycleOwner? = null
private val lifecycleOwner get() = _liveCycleOwner!!

/**
 * A simple [Fragment] subclass.
 * Use the [SongPlayer.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongPlayer : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val a: SongViewModel by viewModels()

        // initialises the vars
        _binding = FragmentSongPlayerBinding.inflate(inflater, container, false)
        _api = a
        _liveCycleOwner = viewLifecycleOwner

        //Listener
        binding.playButton.setOnClickListener { playButtonOnClick() }
        binding.search.setOnClickListener { searchButton() }
        binding.skipButton.setOnClickListener { api.skipSong() }
        binding.backButton.setOnClickListener { api.backSong() }
        binding.searchText.setOnKeyListener { _, keyCode, _ -> search(keyCode, binding.searchText.text.toString()) }

        return binding.root
    }

    /**
     * opens keyboard and shows searchbar if searchbar is invisible
     * else closes the keyboard and hides the searchbar
     */
    private fun searchButton() {
        if (binding.searchTextLayout.isVisible) {
            search(
                KeyEvent.KEYCODE_ENTER,
                binding.searchText.text.toString()
            ) // also executes the search fun if is closes the searchbar
            closeKeyboard()
        } else
            openKeyboard()
    }

    /**
     * the actual fun who closes the keyboard
     */
    private fun closeKeyboard() {
        val songs = binding.songs
        val margin = songs.layoutParams as ViewGroup.MarginLayoutParams

        margin.topMargin = 0
        songs.layoutParams = margin

        binding.searchTextLayout.visibility = View.INVISIBLE

        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    /**
     * the actual fun who opens the keyboard
     */
    private fun openKeyboard() {
        val songs = binding.songs
        val search = binding.search
        val margin = songs.layoutParams as ViewGroup.MarginLayoutParams

        margin.topMargin = search.height + 2 * search.marginTop
        songs.layoutParams = margin

        binding.searchTextLayout.visibility = View.VISIBLE
        binding.searchText.requestFocus()

        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /**
     * updates the SongsRecycleAdapter with the songs with contains the text from the search bar
     */
    private fun search(keyCode: Int, input: String): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            closeKeyboard()
            api.getSongs().observe(lifecycleOwner, { song ->
                val songs = mutableListOf<GetSongs>()

                song.forEach { e ->
                    if (e.name.toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT)))
                        songs.add(e)
                }

                binding.songs.adapter = SongsRecycleAdapter(songs.toList())
            })
        }

        return true
    }

    /**
     * if currently a song is playing it pause it
     * else if a song is on pause is start it again
     * else is just do nothing
     */
    private fun playButtonOnClick() {
        if (playing == null) return

        if (playing!!) {
            api.pauseSong()
            playing = false
            binding.playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            val playSong = PlaySong(
                "",
                ""
            )
            MainScope().launch {
                SongApi.retrofitService.playSong(ConnectActivity.baseUrl + "/playSong", playSong)
            }
            playing = true
            binding.playButton.setImageResource(R.drawable.ic_baseline_pause_24)
        }
    }

    /**
     * load the songs
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val songs = binding.songs
        val connecting = binding.connecting

        songs.layoutManager = LinearLayoutManager(requireContext())

        connecting.visibility = View.VISIBLE
        songs.visibility = View.INVISIBLE

        api.loadSongs(view, "")
        api.getSongs().observe(viewLifecycleOwner, { song ->
            run {
                songs.adapter = SongsRecycleAdapter(song)
                songs.visibility = View.VISIBLE
                connecting.visibility = View.INVISIBLE
            }
        })
    }

    /**
     * start the update runnable
     */
    override fun onStart() {
        super.onStart()
        update.run()
    }

    /**
     * stops the update runnable
     */
    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(update)
    }

    /**
     * gets the current playing song and shows it
     * also get the status of the song and updates the playing var
     */
    private val update: Runnable = Runnable {
        kotlin.run {
            handler.postDelayed(getUpdate(), 2000)

            if (errorMessage.isNotBlank()) {
                binding.currentSong.text = errorMessage
            }

            api.getSong().observe(viewLifecycleOwner, { song ->
                val currentSong = song.currentSong

                if (binding.currentSong.text != currentSong) binding.scrollView.scrollX = 0
                binding.currentSong.text = currentSong

                if (currentSong.isNotBlank()) {
                    playing = song.playing == true && song.pause == false
                    binding.playButton.setImageResource(if (playing!!) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24)
                } else
                    playing = null
            })
        }
    }

    private fun getUpdate(): Runnable {
        return update
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SongPlayer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SongPlayer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var errorMessage = ""
        var playing: Boolean? = null

        /**
         * update the songs with the songs in the given path
         */
        fun updateSongs(path: String) {
            api.loadSongs(binding.root, path)
        }
    }
}
