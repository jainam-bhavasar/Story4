package com.jainam.story2.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.database.BookMetaData
import com.jainam.story2.databinding.ThumbnailItemBinding


class ThumbnailViewAdapter(private val clickListener: ThumbnailClickListener) : ListAdapter<BookMetaData, ThumbnailViewAdapter.ViewHolder>(ThumbnailDiffUtilCallback) {
    lateinit var onClickListenerVariable:OnClickListener

   inner class ViewHolder(val binding: ThumbnailItemBinding) : RecyclerView.ViewHolder(binding.root) , View.OnLongClickListener{
        override fun onLongClick(v: View?): Boolean {
           onClickListenerVariable.onItemLongClick(adapterPosition,v)
           return true
        }

        init {
            itemView.setOnLongClickListener(this)
        }
    }

    fun setOnItemClickListener(onClickListener: OnClickListener) {
        this.onClickListenerVariable= onClickListener
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

     interface OnClickListener {
        fun onItemLongClick(position: Int, v: View?)
    }
}
class ThumbnailClickListener(val clickListener: (BookMetaData) ->Unit){
    fun onClick(bookMetaData: BookMetaData) = clickListener(bookMetaData)
}
object ThumbnailDiffUtilCallback:DiffUtil.ItemCallback<BookMetaData>(){
    override fun areItemsTheSame(oldItem: BookMetaData, newItem: BookMetaData): Boolean {
        return oldItem.thumbnailID==newItem.thumbnailID
    }

    override fun areContentsTheSame(oldItem: BookMetaData, newItem: BookMetaData): Boolean {
        return oldItem == newItem
    }


}