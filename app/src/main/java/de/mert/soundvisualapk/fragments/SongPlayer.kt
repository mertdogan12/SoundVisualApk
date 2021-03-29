package de.mert.soundvisualapk.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import de.mert.soundvisualapk.activities.ConnectActivity
import de.mert.soundvisualapk.activities.recycleviewadapters.SongsRecycleAdapter
import de.mert.soundvisualapk.databinding.FragmentSongPlayerBinding
import de.mert.soundvisualapk.network.SongApi
import de.mert.soundvisualapk.viewmodels.SongViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var _binding: FragmentSongPlayerBinding? = null
private val binding get() = _binding!!

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
        _binding = FragmentSongPlayerBinding.inflate(inflater, container, false)

        binding.playButton.setOnClickListener {
            stopSong()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val songs = binding.songs
        val api: SongViewModel by viewModels()

        songs.layoutManager = LinearLayoutManager(requireContext())

        binding.connecting.visibility = View.VISIBLE
        binding.songs.visibility = View.INVISIBLE

         api.loadSongs(view)
         api.getSongs(view).observe(viewLifecycleOwner, { song ->
             run {
                 binding.songs.adapter = SongsRecycleAdapter(song)
                 binding.connecting.visibility = View.INVISIBLE
                 binding.songs.visibility = View.VISIBLE
             }
         })
    }

    private fun stopSong() {
        MainScope().launch {
            SongApi.retrofitService.stopSong(ConnectActivity.baseUrl + "/stopSong")
        }
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
    }
}