package com.jainam.story2.player.playerSettings.playerSettingsNavigation

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import com.jainam.story2.utils.NavigateBy
import kotlinx.android.synthetic.main.navigate_by_text_view.view.*

class NavigateByAdapter(navigateBy: NavigateBy) :RecyclerView.Adapter<NavigateByAdapter.ViewHolder>(){

   val data:List<NavigateBy> =  NavigateBy.values().toList()

    var selectedNavBy = MutableLiveData(navigateBy)
    class ViewHolder(  itemView:View): RecyclerView.ViewHolder(itemView){
        val navigateByTextView:TextView = itemView.navigateByTextView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.navigate_by_text_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.navigateByTextView.text = data[position].toString()
        if (data[position] == selectedNavBy.value){
            holder.navigateByTextView.setTextColor(Color.BLUE)
        }else{
            holder.navigateByTextView.setTextColor(Color.BLACK)
        }

        holder.navigateByTextView.setOnClickListener {
            selectedNavBy.postValue(data[position])
            notifyDataSetChanged()
        }

    }



    override fun getItemCount(): Int {
       return data.size
    }
}