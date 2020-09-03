package com.jainam.story2.player

import `in`.championswimmer.sfg.lib.SimpleFingerGestures
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
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jainam.story2.PlayerGraphArgs
import com.jainam.story2.R
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.databinding.FragmentPlayer2Binding
import kotlinx.coroutines.*


class PlayerFragment : Fragment(),View.OnTouchListener {

    private var accessibilityManager: AccessibilityManager? = null

    /* access modifiers changed from: private */
    private lateinit var args: PlayerFragmentArgs

    /* access modifiers changed from: private */
    private lateinit var binding: FragmentPlayer2Binding

    /* access modifiers changed from: private */
    var mGestureDetector: GestureDetectorCompat? = null

    /* access modifiers changed from: private */
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var playerViewModel2Factory: PlayerViewModel2Factory
    /* access modifiers changed from: private */
    private val simpleFingerGestures: SimpleFingerGestures = SimpleFingerGestures()
    private lateinit var gestureDetector:GestureDetector
    /* access modifiers changed from: private */
    private lateinit var viewModelInitialisationJob: Job






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
                val application = requireNotNull(this@PlayerFragment.activity).application
                args = PlayerFragmentArgs.fromBundle(requireArguments())

                val dataSource = BookDatabase.getInstance(application).thumbnailDatabaseDao

                val playerViewModel2Factory = PlayerViewModel2Factory(Uri.parse(args.uriAsString),lastPage = args.lastPage,lastPosition = args.lastPosition,application = application,bookDatabaseDao = dataSource,language = args.language,bookLength = args.bookLength)

                val viewModelProvider = ViewModelProvider(findNavController().getViewModelStoreOwner(
                    R.id.playerGraph),playerViewModel2Factory)

                playerViewModel = activity.let {  viewModelProvider.get(PlayerViewModel::class.java)}
                Log.d(tag, "onCreate: playerViewModel initiated")

            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayer2Binding.inflate(inflater,container,false)
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

            gestureDetector = GestureDetector(
                context,
                OnSwipeListener(
                    { playerViewModel.onSwipe(direction = it) },
                    { playerViewModel.onDoubleTap() }
                )
            )
            binding.imageView!!.setOnTouchListener(this@PlayerFragment)
            Log.i(TAG, "onStart: all done")
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouch: ");
        gestureDetector.onTouchEvent(event);
        return true;
    }


}
enum class PlayButtonState{
    PLAYING,PAUSED
}
