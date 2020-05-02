package com.jainam.story2.player

import android.view.View

class VisibilityManager(private val showView:ShowView){

    val playerVisibility :Int get() =  if(showView ==ShowView.SHOW_PLAYER) View.VISIBLE else View.INVISIBLE
    val playerSettingVisibility : Int get()= if(showView == ShowView.SHOW_SETTINGS) View.VISIBLE else View.INVISIBLE

}
enum class ShowView{SHOW_PLAYER,SHOW_SETTINGS}

