package com.jainam.story2.utils

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.jainam.story2.R
import com.jainam.story2.database.Thumbnail
import com.jainam.story2.player.playerSettings.playerSettingsNavigation.Setting

@BindingAdapter("bookName")
fun TextView.setThumbnailName(item:Thumbnail?){
    item?.let {
        text = it.bookName
    }
}

@BindingAdapter("settingName")
fun TextView.setSettingName(item:Setting?){
    item?.let {
        text = when(it){
            Setting.VOICE_SETTING -> "Voice Settings"
            Setting.NAVIGATION_SETTING ->"Navigation Settings"
            Setting.TRANSLATE_SETTING ->"Translate"
        }
    }
}

@BindingAdapter("settingIcon")
fun ImageView.setSettingIcon(item:Setting?){
    item?.let {
        setImageResource(getSettingNameAndBitmap(item).second)
    }
}
fun getSettingNameAndBitmap(setting: Setting):Pair<String,Int>{
    return when(setting){
        Setting.VOICE_SETTING -> Pair("Voice Settings", R.drawable.ic_voice_icon)
        Setting.NAVIGATION_SETTING -> Pair("Navigation Settings",R.drawable.ic_navigation_icon)
        Setting.TRANSLATE_SETTING -> Pair("Navigation Settings",R.drawable.ic_translate_icon)
    }

}

@BindingAdapter("navigateBy")
fun TextView.navigateBy(item:NavigateBy?){
    item?.let {
        text = it.name
    }
}

@BindingAdapter("isSelected")
fun ConstraintLayout.select(item:Boolean?){
    item?.let {
        if (item)setBackgroundColor(Color.WHITE)else {
            val transparentColor = Color.argb(0,0,0,0)
            setBackgroundColor(transparentColor)
        }
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
