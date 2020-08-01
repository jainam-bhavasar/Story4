package com.jainam.story2.player.voice

import android.graphics.Color
import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import kotlinx.android.synthetic.main.voice_item.view.*

class CountrySpecificVoiceChooseAdapter(var voiceList: List<Voice>, val adapterOnClick:(Voice)->Unit, selectedVoice1: Voice)
    : RecyclerView.Adapter<CountrySpecificVoiceChooseAdapter.ViewHolder>() {
    inner class ViewHolder( itemView:View):RecyclerView.ViewHolder(itemView){
        val voiceTextView: TextView = itemView.voiceName
        val networkRequiredImage :ImageView = itemView.networkRequired
        val networkNotRequiredImage :ImageView = itemView.networkNotRequired
        val layout :LinearLayout = itemView.voiceItemLayout
        fun setItem(voice: Voice,position: Int){
            layout.setOnClickListener{

                adapterOnClick(voice)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountrySpecificVoiceChooseAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.voice_item,parent,false)
        return ViewHolder(view)
    }

    val selectedVoice = MutableLiveData(selectedVoice1).apply {
        this.observeForever {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int  = voiceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val string = "${voiceList[position].locale.displayCountry} voice ${position+1}"
        holder.voiceTextView.text = string




        // set background if selected
        if (voiceList[position] == selectedVoice.value){
           holder.apply {
               voiceTextView.setTextColor(Color.WHITE)
               layout.setBackgroundResource(R.drawable.ic_rectangle_selected)
           }
        }else{
            holder.apply {
                voiceTextView.setTextColor(Color.parseColor("#02105C"))
                layout.setBackgroundResource(R.drawable.ic_rectangle)
            }
        }

        if (voiceList[position].isNetworkConnectionRequired){
            holder.networkNotRequiredImage.visibility = View.INVISIBLE
            holder.networkRequiredImage.visibility = View.VISIBLE
            holder.itemView.contentDescription = "Online Voice ${position + 1} "
        }else{
            if (voiceList[position].features.contains("notInstalled")){
                holder.networkNotRequiredImage.visibility = View.VISIBLE
                holder.networkRequiredImage.visibility=View.INVISIBLE
                holder.itemView.contentDescription = "Offline not installed Voice ${position + 1} "
            }else{
                holder.networkNotRequiredImage.visibility = View.INVISIBLE
                holder.networkRequiredImage.visibility=View.INVISIBLE
                holder.itemView.contentDescription = "Offline Voice ${position + 1} "
            }

        }


        //setting on click listener
        holder.setItem(voiceList[position],position)

    }
}