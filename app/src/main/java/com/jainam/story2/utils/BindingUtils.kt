package com.jainam.story2.utils

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.jainam.story2.R
import com.jainam.story2.database.BookMetaData

@BindingAdapter("bookName")
fun TextView.setThumbnailName(item:BookMetaData?){
    item?.let {
        text = it.thumbnailName
    }
}


@BindingAdapter("typeImage")
fun ImageView.setTypeImage(item:String?){
    item?.let {
       if (item == "EPUB") setImageResource(R.drawable.ic_epub)
        else setImageResource(R.drawable.ic_pdf)
    }
}

@BindingAdapter("isSelected")
fun TextView.select(item:Boolean?){
    item?.let {
        if (item)setTextColor(Color.BLACK)else {
            setTextColor(Color.WHITE)
        }
    }
}


