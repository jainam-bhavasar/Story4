package com.jainam.story2.player.music

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.jainam.story2.databinding.FragmentMusicSelectionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class MusicSelectionFragment : DialogFragment() {

    private lateinit var binding: FragmentMusicSelectionBinding


    //creating viewModel
    private lateinit var musicViewModel: MusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Default).launch {
            musicViewModel = activity?.let {
                ViewModelProvider(it).get(MusicViewModel::class.java) }?:throw Exception("invalid activity")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicSelectionBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        //music view model testing
        //background music recyclerView
        binding.rvMusic.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)

        val musicAdapter = MusicAdapter(
            requireContext(),
            selectedMusicId = musicViewModel.selectedMusic.value!!,
            listener = { musicRawId ->
                musicViewModel.playMusicFromStart(musicRawId)
                musicViewModel.selectedMusic.postValue(musicRawId)
                binding.buttonStop.visibility = View.VISIBLE
            })
        binding.rvMusic.adapter = musicAdapter
        musicViewModel.selectedMusic.observe(viewLifecycleOwner, Observer {
            musicAdapter.selectedRawID.postValue(it)
            if (it == -1){
                if (musicViewModel.isMusicPlaying){
                    musicViewModel.stopMusicForever()
                }

            }else{
                if (!musicViewModel.isMusicPlaying){
                    if (!musicViewModel.isPaused){
                         musicViewModel.playSelectedMusic(it)
                    }else{
                        musicViewModel.selectedMusic.postValue(-1)
                    }

                }

            }
        })


        //background music volume change seekbar
        binding.seekbarMusicVolume.apply {
            progress = (musicViewModel.volume.value!!*100).toInt()
            max = 100
            contentDescription = "Background music volume"
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    musicViewModel.volume.postValue(seekbar.progress.toFloat()/100)
                }

            })

        }


        //stop button onclick listener
        binding.buttonStop.setOnClickListener {
            musicViewModel.selectedMusic.postValue(-1)
            it.visibility = View.INVISIBLE

        }

        //stop button visibility
        if (musicViewModel.isMusicPlaying){
            binding.buttonStop.visibility = View.VISIBLE
        }else{
            binding.buttonStop.visibility = View.INVISIBLE
        }


    }

}
