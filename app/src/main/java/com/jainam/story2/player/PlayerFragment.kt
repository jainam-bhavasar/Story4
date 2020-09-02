package com.jainam.story2.player

import `in`.championswimmer.sfg.lib.SimpleFingerGestures
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityManager
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.jainam.story2.PlayerGraphArgs
import com.jainam.story2.databinding.FragmentPlayer2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.Intrinsics


class PlayerFragment : Fragment(),View.OnTouchListener {
    private var accessibilityManager: AccessibilityManager? = null

    /* access modifiers changed from: private */
    var args: PlayerGraphArgs? = null

    /* access modifiers changed from: private */
    private lateinit var binding: FragmentPlayer2Binding

    /* access modifiers changed from: private */
    var mGestureDetector: GestureDetectorCompat? = null

    /* access modifiers changed from: private */
    var playerViewModel: PlayerViewModel? = null

    /* access modifiers changed from: private */
    private val simpleFingerGestures: SimpleFingerGestures = SimpleFingerGestures()
    private lateinit var gestureDetector:GestureDetector
    /* access modifiers changed from: private */
    var viewModelInitialisationJob: Job? = null






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gestureDetector = GestureDetector(context,OnSwipeListener())

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
        binding.imageView!!.setOnTouchListener(this)
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
