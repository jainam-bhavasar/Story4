package com.jainam.story2.utils

import android.graphics.Color
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.jainam.story2.R
import com.jainam.story2.database.Thumbnail
import com.jainam.story2.player.PlayButtonState
import com.jainam.story2.player.PlayerFragment

@BindingAdapter("bookName")
fun TextView.setThumbnailName(item:Thumbnail?){
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


//PLAYER FRAGMENT
//play pause button
@BindingAdapter("toggleButton")
fun ImageView.toggle(item: PlayButtonState?){
    item?.let {
        if (it == PlayButtonState.PLAYING)this.setImageResource(R.drawable.ic_pause_button)else{this.setImageResource(R.drawable.ic_play_button)}

    }
}
