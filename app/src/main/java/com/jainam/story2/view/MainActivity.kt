package com.jainam.story2.view

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.jainam.story2.R
import com.jainam.story2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private lateinit var binding:ActivityMainBinding
 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val window: Window = this.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorSecondarySubtler)


        //binding the activity with its layout
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
    }



}

