package com.jainam.story2.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.database.Thumbnail
import com.jainam.story2.databinding.ThumbnailItemBinding

class ThumbnailViewAdapter(private val clickListener: ThumbnailClickListener) : ListAdapter<Thumbnail, ThumbnailViewAdapter.ViewHolder>(ThumbnailDiffUtilCallback()) {

    class ViewHolder(val binding: ThumbnailItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val bookName: TextView = binding.thumbnailTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val layoutInflater = LayoutInflater.from(parent.context)
       val binding = ThumbnailItemBinding.inflate(layoutInflater,parent,false)
       return ViewHolder(binding)

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemCount != 0) {
            holder.binding.thumbnail = getItem(position)!!
            holder.binding.clickListener = clickListener
            holder.binding.executePendingBindings()
        }
    }


}
class ThumbnailClickListener(val clickListener: (uriAsString:String) ->Unit){
    fun onClick(thumbnail: Thumbnail) = clickListener(thumbnail.uriAsString)
}
class ThumbnailDiffUtilCallback():DiffUtil.ItemCallback<Thumbnail>(){
    override fun areItemsTheSame(oldItem: Thumbnail, newItem: Thumbnail): Boolean {
        return oldItem.bookID == newItem.bookID
    }

    override fun areContentsTheSame(oldItem: Thumbnail, newItem: Thumbnail): Boolean {
        return oldItem == newItem
    }

}