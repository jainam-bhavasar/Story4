package com.jainam.story2.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseApp
import com.jainam.story2.R
import com.jainam.story2.databinding.ActivityMainBinding
import com.jainam.story2.services.LocalService


class MainActivity : AppCompatActivity() {


    private lateinit var binding:ActivityMainBinding



    /** Defines callbacks for service binding, passed to bindService()  */



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
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        //binding the activity with its layout
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
      //  val sharedViewModel = ViewModelProviders.of(this).get(VoiceSelectionMenuViewModel::class.java)
    }



}

