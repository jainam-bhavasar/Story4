package com.jainam.story2.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import com.jainam.story2.database.Thumbnail
import kotlinx.android.synthetic.main.thumbnail_item.view.*

class ThumbnailViewAdapter(private val thumbnails:LiveData<List<Thumbnail>>) : RecyclerView.Adapter<ThumbnailViewAdapter.ViewHolder>() {

    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookName: TextView = itemView.thumbnailTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val layoutInflater = LayoutInflater.from(parent.context)
        val view  = layoutInflater.inflate(R.layout.thumbnail_item,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int = if(thumbnails.value == null) 0 else thumbnails.value!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bookName.text = if(thumbnails.value == null) "" else thumbnails.value!![position].bookName
    }

}