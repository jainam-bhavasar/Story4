package com.jainam.story2.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.jainam.story2.R
import com.jainam.story2.databinding.PlayerFragmentBinding
import com.jainam.story2.player.textCardViewPager.DepthPageTransformer
import com.jainam.story2.player.textCardViewPager.TextCardViewPagerAdapter
import com.jainam.story2.player.voiceSeclection.*
import com.jainam.story2.services.LocalService
import kotlinx.coroutines.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class PlayerFragment : Fragment() {


    private var playButtonState = MutableLiveData(PlayButtonState.PAUSED)




    //service variables
    private lateinit var mService: LocalService
    private var mBound: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var binding: PlayerFragmentBinding

    //selected country live data
    private val selectedCountry = MutableLiveData<String>()
    //selected gender variable
    private val selectedGender = MutableLiveData(Gender.FEMALE)

    // selected voice
    private val selectedVoice = MutableLiveData<Voice>()
    //voice adapter
    private lateinit var voiceAdapter: CountrySpecificVoiceChooseAdapter

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalService.LocalBinder
            mService = binder.getService()
            mBound.postValue(true)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound.postValue(false)
        }
    }


    //variable to keep track if voice button is pressed
    private var voiceButtonState = VoiceButtonState.UNCLICKED
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = PlayerFragmentArgs.fromBundle(requireArguments())

        val intent  =  Intent(requireActivity(),LocalService::class.java).also { intent ->
            intent.putExtra("UriAsString",args.uriString)
        }
        requireActivity().startService(intent)

        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    //voice selection view model
    private lateinit var voiceViewModel: VoiceViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //setting binding
        binding = PlayerFragmentBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this






        return binding.root


    }


    override fun onStart() {
        super.onStart()




        CoroutineScope(Dispatchers.Main).launch{


            // setting the ui
            while (!mBound.value!!) delay(50)


            //set seekbar on mbound
            if (mBound.value!!){

                //on book length changes set the seekbar
                mService.bookLength.observe(viewLifecycleOwner, Observer {
                    binding.pageSeekBarLayout.contentDescription = getString(R.string.page_number_change_slider)
                    binding.pageSeekBarLayout.seekbar.max = it-1
                    binding.pageSeekBarLayout.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {

                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {

                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            if (seekBar != null) {
                                mService.currentPageNumber.postValue(seekBar.progress+1)
                                mService.currentTextPosition.postValue(0)
                            }
                        }

                    })
                })

                //on page text changes set the viewPager's adapter

            }


            //setting the viewpager
            val textCardAdapter = TextCardViewPagerAdapter(listOf(" "))
            binding.textCardViewPager.adapter = textCardAdapter
            binding.textCardViewPager.setPageTransformer(DepthPageTransformer())
            binding.textCardViewPager.background = null
            mService.currentPageTextList.observe(viewLifecycleOwner, Observer {
                binding.textCardViewPager.adapter = TextCardViewPagerAdapter(it)
            })
            val currentCardPosition  = MutableLiveData(binding.textCardViewPager.currentItem)
            binding.textCardViewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    CoroutineScope(Dispatchers.Default).launch {
                        if (playButtonState.value == PlayButtonState.PLAYING){
                            if (mService.currentPageTextList.value != null){
                                mService.speak(position)
                                mService.currentTextPosition.postValue(position)
                            }

                        }
                    }

                }


            })

            currentCardPosition.observe(viewLifecycleOwner, Observer {
                CoroutineScope(Dispatchers.Default).launch {
                    mService.currentTextPosition.postValue(it)
                }
            })

            mService.currentTextPosition.observe(viewLifecycleOwner, Observer {
                binding.textCardViewPager.currentItem = it

            })




            //observer on current page number
            mService.currentPageTextList.observe(viewLifecycleOwner, Observer {
                binding.pageSeekBarLayout.seekbar.progress = mService.currentPageNumber.value!!.minus(1)

                if (playButtonState.value == PlayButtonState.PLAYING){
                    CoroutineScope(Dispatchers.Default).launch {
                        while (mService.currentPageTextList.value == null)delay(50)
                        mService.speak(0)
                    }
                }
            })

            //set the speed seekbar
            while (mService.currentPageTextList.value == null)delay(50)
            if (mService.currentPageTextList.value!=null){
                binding.speedSeekBar.max = 200
                binding.speedSeekBar.progress=80
                binding.speedSeekBar.contentDescription = getString(R.string.voice_speed_change_slider)
                binding.speedSeekBar.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        mService.tts.tts.setSpeechRate(seekBar!!.progress.toFloat()/100)
                        if (playButtonState.value == PlayButtonState.PLAYING){
                            mService.speak(mService.currentTextPosition.value!!)
                        }
                    }

                })
            }

            //setting the pitch seekbar
            if (mService.currentPageTextList.value!=null){
                binding.pitchSeekBar.contentDescription = getString(R.string.voice_pitch_change_slider)
                binding.pitchSeekBar.max = 200
                binding.pitchSeekBar.progress=80
                binding.pitchSeekBar.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        mService.tts.tts.setPitch(seekBar!!.progress.toFloat()/100)
                        if (playButtonState == PlayButtonState.PLAYING){
                            mService.speak(mService.currentTextPosition.value!!)

                        }
                    }

                })
            }





            binding.forward10Seconds.setOnClickListener {
                if (playButtonState.value == PlayButtonState.PLAYING){

                    val newList  = Array(mService.currentPageTextList.value!!.size) { i -> mService.currentPageTextList.value!![i].length}.toList()
                    val resultPos = mService.positionAfterForward10Seconds(newList,mService.currentTextPosition.value!!)

                    if (resultPos == -1){
                        if (mService.currentPageNumber.value!! < mService.bookLength.value!!){
                            mService.currentPageNumber.postValue(mService.currentPageNumber.value!! + 1)
                        }
                    }else{
                        if (resultPos !=  mService.currentTextPosition.value!!){
                            runBlocking { mService.currentTextPosition.postValue(resultPos) }
                            mService.speak(mService.currentTextPosition.value!!)
                        }else{
                            if (resultPos < (newList.size -1)){
                                runBlocking { mService.currentTextPosition.postValue(resultPos+1) }
                                mService.speak(mService.currentTextPosition.value!!)
                            }else{
                                runBlocking { mService.currentTextPosition.postValue(resultPos) }
                                mService.speak(mService.currentTextPosition.value!!)
                            }

                        }

                    }
                }


            }

            binding.backward10secondsButton.setOnClickListener {
                if (playButtonState.value == PlayButtonState.PLAYING){

                    val newList  = Array(mService.currentPageTextList.value!!.size) { i -> mService.currentPageTextList.value!![i].length}.toList()
                    val resultPos = mService.positionAfterBackward10Seconds(newList,mService.currentTextPosition.value!!)

                if (resultPos!=-1){
                    if (currentCardPosition.value!! != resultPos){
                        runBlocking { mService.currentTextPosition.postValue(resultPos) }
                        mService.speak(mService.currentTextPosition.value!!)

                    }else{
                        if (resultPos > 0){
                            runBlocking { mService.currentTextPosition.postValue(resultPos-1) }
                            mService.speak(mService.currentTextPosition.value!!)
                        }

                    }
                }





                }


            }

            //getting the voice view model
            val voiceViewModelFactory = VoiceViewModelFactory(mService.tts,mService.language.value!!)
            voiceViewModel = ViewModelProvider(this@PlayerFragment,voiceViewModelFactory).get(VoiceViewModel::class.java)


            //observing the voices of lang
            voiceViewModel.voicesOfLang.observe(viewLifecycleOwner, Observer {
                val countryChooseAdapter = CountryChooseAdapter(it.keys.toList(),requireContext()) { country ->
                    selectedCountry.postValue(country)
                }
                binding.countryChooserRecyclerView.apply {
                    adapter = countryChooseAdapter
                    layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
                }

            })

            //setting layout managers of voice list
            binding.countryOnlineVoiceListRecyclerView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)

            //observing selected country
            selectedCountry.observe(viewLifecycleOwner, Observer {
                if (it != null){


                    voiceAdapter = CountrySpecificVoiceChooseAdapter(
                        voiceList = voiceViewModel.voicesOf(country = it,lang = mService.language.value!!,gender = selectedGender.value!!),
                        selectedVoice = if (selectedVoice.value != null)selectedVoice.value!! else mService.tts.tts.voice,
                        adapterOnClick = {voice -> selectedVoice.postValue(voice) }
                    )
                    binding.countryOnlineVoiceListRecyclerView.adapter = voiceAdapter
                }
            })

            //voice observing and setting it to the default voice
            selectedVoice.observe(viewLifecycleOwner, Observer {
                Log.d(tag, "onStart: $it")
                if (it != null) {
                    if (it.features.contains("notInstalled") and !it.isNetworkConnectionRequired){
                        Toast.makeText(requireContext(),"Voice not Installed - Using previous selected voice",Toast.LENGTH_SHORT).show()
                    }else if (it.isNetworkConnectionRequired){
                        if (isOnline()){
                            mService.tts.tts.voice = it
                            mService.speak(mService.currentTextPosition.value!!)
                            playButtonState.postValue(PlayButtonState.PLAYING)
                        }
                        else{
                            Toast.makeText(requireContext(),"Network Unavailable",Toast.LENGTH_SHORT).show()
                        }

                    }

                }
            })

            playButtonState.observe(viewLifecycleOwner, Observer {
                if (it == PlayButtonState.PLAYING){
                    mService.speak(binding.textCardViewPager.currentItem)
                    binding.playPauseButton.setImageResource(R.drawable.ic_pause_button)
                }else{
                    mService.stopTTS()
                    binding.playPauseButton.setImageResource(R.drawable.ic_play_button)
                }
            })

            //changing visibility of play button to appear
            binding.progressBarPlayButton.visibility = View.INVISIBLE
            binding.playPauseButton.visibility = View.VISIBLE


        }




        //previous button on click listener
        // tells it to go on page to next page

        CoroutineScope(Dispatchers.Main).launch{
            //on click listener on next page
            binding.nextPageButton.setOnClickListener {
                if (mBound.value!!){
                    CoroutineScope(Dispatchers.Main).launch {
                        if (mService.bookLength.value!! != 0){
                            if (mService.currentPageNumber.value!! < mService.bookLength.value!!){
                                mService.currentPageNumber.postValue(mService.currentPageNumber.value!! + 1)
                            }
                        }
                    }


                }
            }

        }


        //previous button on click listener
        // tells it to go on page back
        CoroutineScope(Dispatchers.Main).launch{
            //on click listener on next page
            binding.prevPageButton.setOnClickListener {
                if (mBound.value!!){
                    CoroutineScope(Dispatchers.Main).launch {
                        if (mService.bookLength.value!! != 0){
                            if (mService.currentPageNumber.value!! > 1){
                                mService.currentPageNumber.postValue(mService.currentPageNumber.value!! - 1)
                            }
                        }
                    }


                }
            }

        }



        //ply pause button on click listener
        //  tells it to start speaking from the current viewpager position
        binding.playPauseButton.setOnClickListener {

            if (mBound.value!!){
                if (playButtonState.value == PlayButtonState.PAUSED){

                    playButtonState.postValue( PlayButtonState.PLAYING)
                }else{

                    playButtonState.postValue(PlayButtonState.PAUSED)

                }
            }
        }

        //voice button onclick listener
        //open voice change settings
        binding.voiceButton.setOnClickListener {
            if (mBound.value!!){
                //first set the visibility of the viewpager to invisible
                if (voiceButtonState == VoiceButtonState.UNCLICKED){
                    binding.textCardViewPager.visibility = View.INVISIBLE
                    binding.voiceChoosingLinearLayout.visibility = View.VISIBLE
                    voiceButtonState = VoiceButtonState.CLICKED
                }else{
                    binding.textCardViewPager.visibility = View.VISIBLE
                    binding.voiceChoosingLinearLayout.visibility = View.INVISIBLE
                    voiceButtonState = VoiceButtonState.UNCLICKED
                }


            }
        }






    }



    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy: mbound value is ${mBound.value}")
        if (mBound.value!!){
            mService.stopTTS()
            requireActivity().unbindService(connection)
            mService.stopSelf()
            mBound.postValue(false)
        }
    }


    fun isOnline() = runBlocking {

        return@runBlocking try {
            withContext(Dispatchers.IO){
                val timeoutMs = 1500
                val sock = Socket()
                val sockaddress: SocketAddress = InetSocketAddress("8.8.8.8", 53)
                sock.connect(sockaddress, timeoutMs)
                sock.close()
                true
            }

        } catch (e: IOException) {
            false
        }
    }


}




enum class PlayButtonState{
    PLAYING,PAUSED
}

enum class VoiceButtonState{
    CLICKED,UNCLICKED
}
