package com.jainam.story2.player.music

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import kotlinx.android.synthetic.main.fragment_music_selection_dialog_item.view.*

class MusicAdapter(val context:Context,selectedMusicId:Int,val listener:(musicRawId:Int) ->Unit) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    private val musicList  = listOf(R.raw.piano,R.raw.saxophone,R.raw.forest,R.raw.storm)
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val musicTextView : TextView = itemView.tvMusic

        fun setItem(position: Int){

            itemView.setOnClickListener {
                selectedRawID.postValue(musicList[position])
                listener(musicList[position])
            }

        }
    }

    val selectedRawID  = MutableLiveData(selectedMusicId).apply {
        observeForever {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.fragment_music_selection_dialog_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = musicList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //setting text
        holder.musicTextView.text = context.resources.getResourceEntryName(musicList[position]).capitalize()


        //changing style on click
        if (selectedRawID.value == musicList[position]){
            holder.musicTextView.setTextColor(Color.WHITE)
            holder.itemView.background = context.getDrawable(R.drawable.ic_rectangle_selected)
        }else{
            holder.musicTextView.setTextColor(context.resources.getColor(R.color.colorPrimary))
            holder.itemView.background = context.getDrawable(R.drawable.ic_rectangle)
        }

        //setting onclick listener
        holder.setItem(position)
    }
}