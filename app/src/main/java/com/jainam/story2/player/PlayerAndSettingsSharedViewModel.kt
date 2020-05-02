package com.jainam.story2.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jainam.story2.utils.NavigateBy

class PlayerAndSettingsSharedViewModel : ViewModel() {
   private val navigationBy  = MutableLiveData<NavigateBy>(NavigateBy.PAGE)
    fun setNavBy(navigateBy: NavigateBy){
        this.navigationBy.postValue(navigateBy)
    }
}