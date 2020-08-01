package com.jainam.story2.player

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.jainam.story2.R
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.databinding.FragmentPlayer2Binding
import com.jainam.story2.player.music.MusicViewModel
import kotlinx.android.synthetic.main.fragment_player2.*
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class PlayerFragment2 : Fragment() {
    //binding
    private lateinit var binding:FragmentPlayer2Binding
    //args
    private lateinit var args: PlayerFragment2Args
    //player view model 2
    private lateinit var playerViewModel2: PlayerViewModel2

    private lateinit var viewModelInitialisationJob:Job
    //text card view pager adapter
    private lateinit var textCardViewPagerAdapter: TextCardViewPagerAdapter

    //PLAY BUTTON INITIAL STATE
    private val playButtonState = MutableLiveData(PlayButtonState.PAUSED)
    //is speaking save variable
    private var isSpeakingWhileGone = false
    //saved states
    private var currentPageNum = 1
    private var currentPosition = 0


    private lateinit var accessibilityManager: AccessibilityManager

    //media player
   private lateinit var musicSharedViewModel: MusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //call back for not going back while view-model is initialising
        requireActivity().onBackPressedDispatcher.addCallback(this){
            Toast.makeText(context, "Please wait while document loads up", Toast.LENGTH_SHORT).show()
        }
        //job to start view model initialisation\
        accessibilityManager  = requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        viewModelInitialisationJob =  CoroutineScope(Dispatchers.Main).launch{

            withContext(Dispatchers.Default){
                val application = requireNotNull(this@PlayerFragment2.activity).application
                args = PlayerFragment2Args.fromBundle(requireArguments())

                val dataSource = BookDatabase.getInstance(application).thumbnailDatabaseDao

                val playerViewModel2Factory = PlayerViewModel2Factory(Uri.parse(args.uriAsString),lastPage = args.lastPage,lastPosition = args.lastPosition,application = application,bookDatabaseDao = dataSource,language = args.language,bookLength = args.bookLength)

                val viewModelProvider = ViewModelProvider(findNavController().getViewModelStoreOwner(R.id.playerGraph),playerViewModel2Factory)

                playerViewModel2 = activity.let {  viewModelProvider.get(PlayerViewModel2::class.java)}
                Log.d(tag, "onCreate: playerViewModel initiated")

                currentPosition = playerViewModel2.currentPageNum.value?:0
                currentPageNum = playerViewModel2.currentPageNum.value?:1

                musicSharedViewModel = activity?.let {
                    ViewModelProvider(it).get(MusicViewModel::class.java) }?:throw Exception("invalid activity")
                }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayer2Binding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.currentPage = 1
        binding.bookLen = 1
        return binding.root


    }


    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.Main).launch {
            // getting the playerViewModel 2
            if (!viewModelInitialisationJob.isCompleted) {
                 viewModelInitialisationJob.join()
            }

            //callback for letting you go back while loading is finished
            requireActivity().onBackPressedDispatcher.addCallback(this@PlayerFragment2){
                findNavController().popBackStack()
            }
            binding.playerViewModel = playerViewModel2

            playerViewModel2.currentPageNum.postValue(currentPageNum)
            playerViewModel2.currentPageTextList.postValue(playerViewModel2.getPageList(currentPageNum))

            if (playerViewModel2.currentPosition.value!! != currentPosition) {
                playerViewModel2.currentPosition.postValue(currentPosition)
            }

            //initialising the view pager
            binding.textCardViewPager.apply {
                textCardViewPagerAdapter = TextCardViewPagerAdapter(playerViewModel2.currentPageTextList.value!!)
                adapter = textCardViewPagerAdapter
                setPageTransformer(DepthPageTransformer())
                background = null
                registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        playerViewModel2.currentPosition.postValue(position)
                    }
                })
            }

            //observing current page number and setting list of viewpager to it
            playerViewModel2.currentPageTextList.observe(viewLifecycleOwner, Observer {

                if (it != null){
                    textCardViewPagerAdapter.data = it
                    textCardViewPager.adapter!!.notifyDataSetChanged()
                    binding.pageSeekBarLayout.progress = playerViewModel2.currentPageNum.value!! -1
                }

            })

            //setting the book length
            binding.bookLen = playerViewModel2.bookLength

            // seekbar initialisation
            binding.pageSeekBarLayout.apply {
                contentDescription = getString(R.string.page_number_change_slider)
                seekbar.max = playerViewModel2.bookLength-1
                seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (accessibilityManager.isEnabled and accessibilityManager.isTouchExplorationEnabled){
                            playerViewModel2.currentPageNum.postValue(progress+1)
                            playerViewModel2.currentPageTextList.postValue(playerViewModel2.getPageList(seekbar.progress +1))
                            playerViewModel2.currentPosition.postValue(0)
                        }

                       //changing the current page number variable in the layout xml indicate the current page
                       binding.currentPage = progress+1
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        if (seekBar != null) {
                            playerViewModel2.currentPageNum.postValue(seekBar.progress+1)
                            playerViewModel2.currentPageTextList.postValue(playerViewModel2.getPageList(seekbar.progress +1))
                            playerViewModel2.currentPosition.postValue(0)
                        }
                    }

                })
            }

            //observing current position to speak when changed
            playerViewModel2.currentPosition.observe(viewLifecycleOwner, Observer {pos ->

                binding.textCardViewPager.currentItem = pos
                if (!isSpeakingWhileGone){
                    if (binding.playButtonState == PlayButtonState.PLAYING) {
                        if (playerViewModel2.tts.tts.isSpeaking){
                            playerViewModel2.stopSpeaking()
                        }
                         playerViewModel2.speak()
                    }
                }else{
                    isSpeakingWhileGone = false
                }

            })
            //10 seconds backwards
            binding.backward10secondsButton.setOnClickListener {
                playerViewModel2.backward10Seconds(binding.textCardViewPager.currentItem)
            }

            //10 seconds forward
            binding.forward10Seconds.setOnClickListener {
                playerViewModel2.forward10Seconds(binding.textCardViewPager.currentItem)
            }


            //player view model speak state observe and change play and pause button state
            playerViewModel2.speakState.observe(viewLifecycleOwner, Observer { state ->
                if (state == SpeakState.SPEAKING){
                    //change the play button image to paused one
                    binding.playButtonState = PlayButtonState.PLAYING


                    //speak tts
                    playerViewModel2.speak()

                    //play music if selected
                    if (musicSharedViewModel.selectedMusic.value!! != -1){
                        musicSharedViewModel.playSelectedMusic(musicSharedViewModel.selectedMusic.value!!)
                    }
                }else{
                    //change play button image to play logo
                    binding.playButtonState = PlayButtonState.PAUSED

                    //stop  tts
                    playerViewModel2.stopSpeaking()
                    //stopping background music
                    musicSharedViewModel.isMusicPlaying.let {
                        if (it)musicSharedViewModel.pauseMusic()
                    }
                }
            })
            //play button onclick
            binding.playPauseButton.setOnClickListener{
                if (playerViewModel2.speakState.value == SpeakState.SPEAKING)playerViewModel2.speakState.postValue(SpeakState.PAUSED)
                else {playerViewModel2.speakState.postValue(SpeakState.SPEAKING)}
            }

            binding.musicButton?.setOnClickListener {
                findNavController().navigate(PlayerFragment2Directions.actionPlayerFragment2ToMusicSelectionFragment())
            }

            binding.buttonVoice?.setOnClickListener {
                findNavController().navigate(PlayerFragment2Directions.actionPlayerFragment2ToVoiceFragment(playButtonState.value!!))
            }
            //play button
            binding.apply {
                progressBarPlayButton.visibility = View.INVISIBLE
                playPauseButton.visibility = View.VISIBLE
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (playButtonState.value == PlayButtonState.PLAYING){
            binding.playPauseButton.setImageResource(R.drawable.ic_pause_button)
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel2.currentPosition.observe(viewLifecycleOwner, Observer {
            if (currentPosition != it) {
                currentPosition = it
            }
        })

        playerViewModel2.currentPageNum.observe(viewLifecycleOwner, Observer {
            if (currentPageNum != it) {
                currentPageNum = it
            }
        })
        isSpeakingWhileGone = true
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel2.stopSpeaking()
        musicSharedViewModel.pauseMusic()
    }



}

enum class PlayButtonState{
    PLAYING,PAUSED
}

