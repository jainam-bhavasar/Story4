package com.jainam.story2.player.playerSettings.playerSettingsNavigation.navigateBy

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jainam.story2.utils.NavigateBy

class NavigateByViewModel:ViewModel() {

    //this is the starting navigateBy setting which is set default to navigate by sentence
    val selectedNavigateBy = MutableLiveData( NavigateBy.SENTENCE)

}