package com.jainam.story2.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.jainam.story2.R
import com.jainam.story2.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {


    private lateinit var binding:ActivityMainBinding
 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        //binding the activity with its layout
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )





    }





}

