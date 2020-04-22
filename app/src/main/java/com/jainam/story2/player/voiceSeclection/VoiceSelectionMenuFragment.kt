package com.jainam.story2.player.voiceSeclection

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.jainam.story2.R
import kotlinx.android.synthetic.main.voice_selection_menu_fragment.*

class VoiceSelectionMenuFragment : Fragment() {

    companion object {
        fun newInstance() = VoiceSelectionMenuFragment()
    }

    private lateinit var viewModel: VoiceSelectionMenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.voice_selection_menu_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(VoiceSelectionMenuViewModel::class.java)
        val application = requireNotNull(this.activity).application

        helloTextView.setOnClickListener{
            viewModel.getVoices()
        }

        var countryChooserAdapter  = CountryChooseAdapter(listOf("yes"))
        countryChooserRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        countryChooserRecyclerView.adapter = countryChooserAdapter

        var countrySpecificVoiceChooseAdapter = CountrySpecificVoiceChooseAdapter(MutableLiveData())
        countryVoiceListRecyclerView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        countryVoiceListRecyclerView.adapter = countrySpecificVoiceChooseAdapter
        viewModel.allVoices.observe(viewLifecycleOwner, Observer {
            //setting country choosing adapter
            val countryList = it.keys.toList()
            countryChooserAdapter  = CountryChooseAdapter(countryList)
            countryChooserRecyclerView.adapter = countryChooserAdapter

            countrySpecificVoiceChooseAdapter = CountrySpecificVoiceChooseAdapter(
            MutableLiveData(it[countryList[0]]!!.toList()))
            countryVoiceListRecyclerView.adapter = countrySpecificVoiceChooseAdapter
        })


    }

}
