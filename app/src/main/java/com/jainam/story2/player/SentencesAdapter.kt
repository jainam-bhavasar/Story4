package com.jainam.story2.player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import kotlinx.android.synthetic.main.sentence_list_item.view.*

class SentencesAdapter(private val items: ArrayList<String>, private val context: Context) :
    RecyclerView.Adapter<SentencesAdapter.ViewHolder>() {

    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Holds the TextView that will add each animal to
        val tvAnimalType: TextView = itemView.sentence_list_recycler_view_item_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.sentence_list_item, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAnimalType.text = items[position]
    }

}