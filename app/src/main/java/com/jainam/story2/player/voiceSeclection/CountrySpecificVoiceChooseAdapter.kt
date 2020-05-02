package com.jainam.story2.player.voiceSeclection

import android.content.Context
import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import com.jainam.story2.player.PlayButtonState
import com.jainam.story2.services.LocalService
import com.jainam.story2.utils.TTS
import kotlinx.android.synthetic.main.voice_item.view.*

class CountrySpecificVoiceChooseAdapter(private val voiceList: List<Voice>, val adapterOnClick:(Voice)->Unit, private val selectedVoice: Voice)
    : RecyclerView.Adapter<CountrySpecificVoiceChooseAdapter.ViewHolder>() {
    inner class ViewHolder( itemView:View):RecyclerView.ViewHolder(itemView){
        val voiceTextView: TextView = itemView.voiceName
        val networkRequiredImage :ImageView = itemView.networkRequired
        val networkNotRequiredImage :ImageView = itemView.networkNotRequired
        val layout :LinearLayout = itemView.voiceItemLayout
        fun setItem(voice: Voice,position: Int){
            layout.setOnClickListener{
                selectedPosition.postValue(position)
                adapterOnClick(voice)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountrySpecificVoiceChooseAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.voice_item,parent,false)
        return ViewHolder(view)
    }

    //returns the position of the already selected voice if that voice exists or returns null
    private fun alreadySelectedVoicePosition():Int{
        for (i in voiceList.indices){
            if (voiceList[i] == selectedVoice){
                return i
            }
        }
        return -1
    }
    val selectedPosition = MutableLiveData<Int>(alreadySelectedVoicePosition()).apply {
        this.observeForever {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int  = voiceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val string = "${position+1}"
        holder.voiceTextView.text = string


//            val intent = Intent()
//            intent.apply {
//                action = "com.android.settings.TTS_SETTINGS"
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//            context.startActivity(intent)

        // set background if selected
        if (position == selectedPosition.value!!){
            holder.layout.setBackgroundResource(R.drawable.pressed_voice_background)
        }else{
            holder.layout.setBackgroundResource(R.drawable.voice_background)
        }
        if (voiceList[position].isNetworkConnectionRequired){
            holder.networkNotRequiredImage.visibility = View.INVISIBLE
            holder.networkRequiredImage.visibility=View.VISIBLE
        }else{
            holder.networkNotRequiredImage.visibility = View.VISIBLE
            holder.networkRequiredImage.visibility=View.INVISIBLE
        }

        //setting on click listener
        holder.setItem(voiceList[position],position)

    }
}