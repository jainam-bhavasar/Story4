package com.jainam.story2.player

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jainam.story2.R
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.databinding.FragmentPlayer2Binding
import kotlinx.coroutines.*


class PlayerFragment : Fragment(), View.OnTouchListener {

    private var accessibilityManager: AccessibilityManager? = null

    /* access modifiers changed from: private */
    private lateinit var args: PlayerFragmentArgs

    /* access modifiers changed from: private */
    private lateinit var binding: FragmentPlayer2Binding

    /* access modifiers changed from: private */
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var gestureDetector: GestureDetector

    /* access modifiers changed from: private */
    private lateinit var viewModelInitialisationJob: Job

    private lateinit var simpleTwoFingerDoubleTapDetector: SimpleTwoFingerDoubleTapDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //call back for not going back while view-model is initialising
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(context, "Please wait while document loads up", Toast.LENGTH_SHORT)
                .show()
        }
        //job to start view model initialisation\
        accessibilityManager =
            requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        viewModelInitialisationJob = CoroutineScope(Dispatchers.Default).launch {


            val application = requireNotNull(this@PlayerFragment.activity).application
            args = PlayerFragmentArgs.fromBundle(requireArguments())

            val dataSource = BookDatabase.getInstance(application).thumbnailDatabaseDao

            val playerViewModel2Factory = PlayerViewModel2Factory(
                Uri.parse(args.uriAsString),
                lastPage = args.lastPage,
                lastPosition = args.lastPosition,
                application = application,
                bookDatabaseDao = dataSource,
                language = args.language,
                bookLength = args.bookLength,
                entity =args.entity
            )

            val viewModelProvider = ViewModelProvider(
                findNavController().getViewModelStoreOwner(
                    R.id.playerGraph
                ), playerViewModel2Factory
            )
            Log.d("pvm", "onCreate: pvm not initalised ")
            playerViewModel = activity.let { viewModelProvider.get(PlayerViewModel::class.java) }
            Log.d("pvm", "onCreate: playerViewModel initiated")


        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayer2Binding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.Main).launch {
            // getting the playerViewModel 2
            if (!viewModelInitialisationJob.isCompleted) {
                viewModelInitialisationJob.join()
            }
            requireActivity().onBackPressedDispatcher.addCallback(this@PlayerFragment) {
                findNavController().popBackStack()
            }
            gestureDetector = GestureDetector(
                context,
                OnSwipeListener(
                    { playerViewModel.onSwipe(direction = it) },
                    { playerViewModel.onDoubleTap() }
                )
            )

        }
        simpleTwoFingerDoubleTapDetector = object : SimpleTwoFingerDoubleTapDetector() {
            override fun onTwoFingerDoubleTap() {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.btnSettings?.visibility = View.VISIBLE
                    delay(2000)
                    binding.btnSettings?.visibility = View.GONE
                }
            }
        }

        binding.btnSettings?.setOnClickListener {
            findNavController().navigate(
                PlayerFragmentDirections.actionPlayerFragmentToVoiceFragment(
                    playerViewModel.speakState
                )
            )
        }
        binding.imageView!!.setOnTouchListener(this@PlayerFragment)
        Log.i(TAG, "onStart: all done")


    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouch: ")
        gestureDetector.onTouchEvent(event)
        simpleTwoFingerDoubleTapDetector.onTouchEvent(event)
        return true
    }


}

