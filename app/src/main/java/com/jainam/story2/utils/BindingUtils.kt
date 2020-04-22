package com.jainam.story2.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.jainam.story2.database.Thumbnail

@BindingAdapter("bookName")
fun TextView.setThumbnailName(item:Thumbnail?){
    item?.let {
        text = it.bookName
    }
}