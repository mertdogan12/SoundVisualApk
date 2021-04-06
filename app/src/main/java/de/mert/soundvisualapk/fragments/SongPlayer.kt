package de.mert.soundvisualapk.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import de.mert.soundvisualapk.activities.recycleviewadapters.SongsRecycleAdapter
import de.mert.soundvisualapk.databinding.FragmentSongPlayerBinding
import de.mert.soundvisualapk.network.GetSongs
import de.mert.soundvisualapk.viewmodels.SongViewModel

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

        _binding = FragmentSongPlayerBinding.inflate(inflater, container, false)
        _api = a
        _liveCycleOwner = viewLifecycleOwner

        binding.playButton.setOnClickListener {
            stopSong()
        }

        return binding.root
    }

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

    override fun onStart() {
        super.onStart()
        update.run()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(update)
    }

    private fun stopSong() {
        api.stopSong()
    }

    private val update: Runnable = Runnable {
        kotlin.run {
            handler.postDelayed(getUpdate(), 2000)

            if (errorMessage.isNotBlank()) {
                binding.currentSong.text = errorMessage
            }

            api.getSong().observe(viewLifecycleOwner, { song ->
                binding.currentSong.text = song.currentSong
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

        fun updateSongs(path: String) {
            api.loadSongs(binding.root, path)
            api.getSongs().observe(lifecycleOwner, { songs ->
                var backPath = ""
                val mutableSongs = songs.toMutableList()
                val firstElementPath: MutableList<String> = songs[0].path.split("/").toMutableList()

                firstElementPath.removeLast()
                if (firstElementPath.isNotEmpty()) firstElementPath.removeLast()
                backPath = firstElementPath.joinToString("/")

                val back: GetSongs = GetSongs(
                    "dir",
                    "..",
                    backPath
                )

                mutableSongs.add(0, back)

                binding.songs.adapter = SongsRecycleAdapter(mutableSongs.toList())
            })
        }
    }
}
