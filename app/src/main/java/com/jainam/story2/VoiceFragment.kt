package com.jainam.story2

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.databinding.FragmentVoiceBinding
import com.jainam.story2.player.PlayButtonState
import com.jainam.story2.player.PlayerViewModel
import com.jainam.story2.player.voice.CountryChooseAdapter
import com.jainam.story2.player.voice.CountrySpecificVoiceChooseAdapter
import com.jainam.story2.player.voice.VoiceViewModel
import com.jainam.story2.player.voice.VoiceViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress


/**
 * A simple [Fragment] subclass.
 */
class VoiceFragment : Fragment() {

    private lateinit var binding:FragmentVoiceBinding
    private lateinit var playButtonState: PlayButtonState
    //player view model
    val playerViewModel: PlayerViewModel by navGraphViewModels(R.id.playerGraph)
    private lateinit var voiceViewModel:VoiceViewModel
    private lateinit var accessibilityManager:AccessibilityManager


    //country voices adapter
    private lateinit var countrySpecificVoiceChooseAdapter: CountrySpecificVoiceChooseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = VoiceFragmentArgs.fromBundle(requireArguments())
        playButtonState = args.playButtonState
        val application = requireNotNull(this.activity).application

        val dataSource = BookDatabase.getInstance(application).thumbnailDatabaseDao
        //initialising voice view model
        val voiceViewModelFactory = VoiceViewModelFactory(voice = playerViewModel.tts.tts.voice,tts = playerViewModel.tts,bookDatabaseDao = dataSource)
        voiceViewModel = ViewModelProvider(this,voiceViewModelFactory).get(VoiceViewModel::class.java)

        accessibilityManager  = requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoiceBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        //speed seekbar
        binding.speedSeekBar.apply {
            contentDescription = "Speed"
            max = 200
            progress = playerViewModel.speed.value
            seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (accessibilityManager.isEnabled and accessibilityManager.isTouchExplorationEnabled){
                        playerViewModel.speed.value = seekbar.progress
                        playerViewModel.speak()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    playerViewModel.speed.value = seekbar.progress
                        playerViewModel.speak()

                }

            })
        }

        //pitch seekbar
        binding.pitchSeekBar.apply {
            contentDescription = "Pitch"
            max = 200
            progress = playerViewModel.pitch.value
            seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (accessibilityManager.isEnabled and accessibilityManager.isTouchExplorationEnabled){
                        playerViewModel.pitch.value = seekbar.progress
                        playerViewModel.speak()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    playerViewModel.pitch.value = seekbar.progress
                        playerViewModel.speak()

                }


            })


        }

        //observing list of voices and setting accent and voice list adapter to them
        voiceViewModel.voicesOfLang.observe(viewLifecycleOwner, Observer {

            binding.rvAccent.apply {
                layoutManager  = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                adapter =CountryChooseAdapter(it.keys.toList(),context,alreadySelectedCountry = voiceViewModel.country.value!!)
                { voiceViewModel.country.postValue(it)}
            }

            //observing selected country and setting the voice list to it
            binding.rvVoice.apply {
                layoutManager  = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

                countrySpecificVoiceChooseAdapter = CountrySpecificVoiceChooseAdapter(
                    (it[voiceViewModel.country.value!!] ?: error("")).toList(),
                    {
                        //if its offline voice and not installed
                        Log.d("tts", "onStart: $it")
                        if (!it.isNetworkConnectionRequired){
                            Log.d("tts", "onStart: tts network not required")
                            playerViewModel.stopSpeaking()
                            //if its not installed show dialog and go to download settings of text to speech
                            if (voiceViewModel.isOfflineVoiceNotInstalled(it)){
                                showDialogForDownloadingOfflineVoices()
                            }
                            // it its installed
                            else{
                                voiceViewModel.voice = it
                                playerViewModel.tts.tts.voice = it
                                playerViewModel.speak()
                                countrySpecificVoiceChooseAdapter.selectedVoice.postValue(it)
                            }
                        }
                        else{
                            if (isOnline()){
                                voiceViewModel.voice = it
                                playerViewModel.tts.tts.voice = it
                                playerViewModel.speak()
                                countrySpecificVoiceChooseAdapter.selectedVoice.postValue(it)
                            }else{
                                showDialogForShowingNetworkIsRequired()
                            }
                        }

                    },
                    voiceViewModel.voice)
                adapter = countrySpecificVoiceChooseAdapter
            }

        })


        //voices of language and country updates
        voiceViewModel.voicesOfLangAndCountry.observe(viewLifecycleOwner, Observer {
            countrySpecificVoiceChooseAdapter.voiceList = it
            countrySpecificVoiceChooseAdapter.notifyDataSetChanged()
        })


    }

    override fun onDestroy() {
        super.onDestroy()
        if (playButtonState == PlayButtonState.PAUSED){
            playerViewModel.stopSpeaking()
        }
    }

    private fun isOnline(): Boolean  = runBlocking {
        return@runBlocking withContext(Dispatchers.IO) {
            try {
                val timeoutMs = 1500
                val sock = Socket()
                val socketAddress: SocketAddress = InetSocketAddress("8.8.8.8", 53)
                sock.connect(socketAddress, timeoutMs)
                sock.close()
                return@withContext true
            } catch (e: IOException) {
                return@withContext false
            }
        }
    }
    private fun showDialogForDownloadingOfflineVoices(){
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Install Voice?")
            builder.setMessage("Install this Voice from Text-to-speech Engine settings")
            builder.apply {
                setPositiveButton("Yes"
                ) { _, _ ->
                    val intent = Intent()
                    intent.apply {
                        action = "com.android.settings.TTS_SETTINGS"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)

                }
                setNegativeButton("No thanks"
                ) { _, _ ->

                }
            }

            // Create the AlertDialog
            builder.create()
        }
        alertDialog!!.show()
    }
    private fun showDialogForShowingNetworkIsRequired(){
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Network not available")
            builder.apply {
                setPositiveButton("OK"
                ) { _, _ ->

                }

            }

            // Create the AlertDialog
            builder.create()
        }
        alertDialog!!.show()
    }

}
