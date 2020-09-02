package com.jainam.story2.player

import `in`.championswimmer.sfg.lib.SimpleFingerGestures
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.jainam.story2.databinding.FragmentPlayer2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.Intrinsics


class PlayerFragment : Fragment() {
    private var accessibilityManager: AccessibilityManager? = null

    /* access modifiers changed from: private */
    var args: PlayerFragmentArgs? = null

    /* access modifiers changed from: private */
    var binding: FragmentPlayer2Binding? = null

    /* access modifiers changed from: private */
    var mGestureDetector: GestureDetectorCompat? = null

    /* access modifiers changed from: private */
    var playerViewModel: PlayerViewModel? = null

    /* access modifiers changed from: private */
    val simpleFingerGestures: SimpleFingerGestures = SimpleFingerGestures()

    /* access modifiers changed from: private */
    var viewModelInitialisationJob: Job? = null






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayer2Binding.inflate(inflater,container,false)
        binding!!.setLifecycleOwner(this)
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
        val playerViewModel2: PlayerViewModel? = playerViewModel

    }


}