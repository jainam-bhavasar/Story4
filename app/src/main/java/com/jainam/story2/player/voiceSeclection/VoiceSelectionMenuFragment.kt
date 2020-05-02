package com.jainam.story2.player.voiceSeclection

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.speech.tts.Voice
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.jainam.story2.R
import kotlinx.android.synthetic.main.voice_list.*
import kotlinx.android.synthetic.main.voice_list.view.*
import kotlinx.android.synthetic.main.voice_selection_menu_fragment.*

class VoiceSelectionMenuFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = VoiceSelectionMenuFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.voice_selection_menu_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        viewModel = activity.run {
//            ViewModelProviders.of(this!!).get(VoiceSelectionMenuViewModel::class.java)
//        }



//        var countryChooserAdapter  = CountryChooseAdapter(listOf("yes"),requireContext())
//        countryChooserRecyclerView.adapter = countryChooserAdapter


//        testButton.setOnClickListener{
//            viewModel.allVoices.postValue(viewModel.getVoicesOfLang("en"))
//        }


        // Getting all the voices asynchronously, this will only happen one time ->thats why
        // all the the country list and voice adapter are declared after getting all the voices
//        viewModel.allVoices.observe(viewLifecycleOwner, Observer {map ->
//            //setting country choosing adapter
//
//
//            //setting the country lists
//            val countryList = map.keys.toList()
//            countryChooserAdapter  = CountryChooseAdapter(countryList,requireContext())
//            countryChooserRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//            countryChooserRecyclerView.adapter = countryChooserAdapter
//
//
//            manVoiceIcon.setOnClickListener {
//
//            }

            //observing the selected country and changing the voice list accordingly




       // })


    }



}


