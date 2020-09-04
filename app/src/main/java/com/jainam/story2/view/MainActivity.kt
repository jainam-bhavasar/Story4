package com.jainam.story2.view

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.jainam.story2.R
import com.jainam.story2.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    private lateinit var binding:ActivityMainBinding



    /** Defines callbacks for service binding, passed to bindService()  */



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val window: Window = this.window


        // clear FLAG_TRANSLUCENT_STATUS flag:


        // finally change the color

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        //binding the activity with its layout
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
      //  val sharedViewModel = ViewModelProviders.of(this).get(VoiceSelectionMenuViewModel::class.java)
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    hideSystemUI()
                }

            }
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }


}

