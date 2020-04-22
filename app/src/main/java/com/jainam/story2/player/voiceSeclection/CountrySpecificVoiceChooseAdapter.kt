package com.jainam.story2.player.voiceSeclection

import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import kotlinx.android.synthetic.main.voice_recycler_view_item.view.*

class CountrySpecificVoiceChooseAdapter(private val voiceListLiveData: MutableLiveData<List<Voice>>) : RecyclerView.Adapter<CountrySpecificVoiceChooseAdapter.ViewHolder>() {
    class ViewHolder( itemView:View):RecyclerView.ViewHolder(itemView){
        val voiceTextView: TextView = itemView.voiceSelectionRecyclerViewItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountrySpecificVoiceChooseAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.voice_recycler_view_item,parent,false)
        return CountrySpecificVoiceChooseAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int  = voiceListLiveData.value?.size?:0

    override fun onBindViewHolder(holder: CountrySpecificVoiceChooseAdapter.ViewHolder, position: Int) {
        holder.voiceTextView.text = voiceListLiveData.value?.get(position).toString()
    }
}