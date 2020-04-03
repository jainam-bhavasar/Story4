package com.jainam.story2.player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jainam.story2.R
import kotlinx.android.synthetic.main.page_list_item.view.*

class PagesAdapter(private val pages: ArrayList<ArrayList<String>>, private val context: Context) :
    RecyclerView.Adapter<PagesAdapter.ViewHolder>() {

    //creating variable(holder) for page sentences
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvPageSentences:RecyclerView = itemView.animal_list_recycler_view
    }

    //creating view holder and inflating the page list item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.page_list_item, parent, false)
        )
    }

    //setting the item count to number of pages
    override fun getItemCount(): Int {
       return pages.size
    }

    //binding page sentences to a linear layout manager and an adapter with input page sentences
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rvPageSentences.layoutManager = LinearLayoutManager(context)
        holder.rvPageSentences.adapter =
            SentencesAdapter(
                pages[position],
                context
            )
    }
}