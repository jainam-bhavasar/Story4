package com.jainam.story2.player.voiceSeclection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import kotlinx.android.synthetic.main.country_chooser_recycler_view_item.view.*

class CountryChooseAdapter(private val countryList: List<String>) : RecyclerView.Adapter<CountryChooseAdapter.ViewHolder>() {

    class ViewHolder( itemView:View):RecyclerView.ViewHolder(itemView){
        val countryTextView: TextView = itemView.countryChooserRecyclerViewItemName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view  = layoutInflater.inflate(R.layout.country_chooser_recycler_view_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int  = countryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.countryTextView.text = countryList[position]
    }

}