package com.jainam.story2.player.music

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MusicViewModel(application: Application) :AndroidViewModel(application){
    val context: Context = application.applicationContext


    //initiating media player
    private var mediaPlayer :MediaPlayer? = null

private val tag = "media"



    var selectedMusic = MutableLiveData(-1)
    //play selected music function
    fun playSelectedMusic(selectedMusic:Int){

        selectedMusic.let {

            mediaPlayer = if (mediaPlayer == null) {
                MediaPlayer.create(context,selectedMusic).apply { start() }
            }else{
                pauseMusic()
                MediaPlayer.create(context,selectedMusic).apply { start() }
            }
            mediaPlayer?.isLooping = true
            isMusicPlaying = true
            isPaused = false
            mediaPlayer?.setVolume(volume.value!!,volume.value!!)
            mediaPlayer?.seekTo(playingPosition)
        }
    }
    fun playMusicFromStart(selectedMusic:Int){
        mediaPlayer = if (mediaPlayer == null) {
            MediaPlayer.create(context,selectedMusic).apply { start() }
        }else{
            pauseMusic()
            MediaPlayer.create(context,selectedMusic).apply { start() }
        }
        mediaPlayer?.isLooping = true
        isMusicPlaying = true
        isPaused = false
        mediaPlayer?.setVolume(volume.value!!,volume.value!!)
    }
    //tracking progress of music
    private var playingPosition :Int = 0
    //pause music
    fun pauseMusic(){

        if (isMusicPlaying){
            playingPosition = if (mediaPlayer != null){
                mediaPlayer!!.currentPosition
            }else{
                0
            }
            mediaPlayer?.pause()
        }
        isMusicPlaying = false
        isPaused = true
    }

    // change volume
    val volume = MutableLiveData(0.5f).apply {
        CoroutineScope(Dispatchers.Main).launch {
            observeForever {
                mediaPlayer?.setVolume(this@apply.value?:0.5f,this@apply.value?:0.5f)
            }
        }
    }
    //is music playing variable
    var isMusicPlaying : Boolean = false

    //is music paused
    var isPaused:Boolean = true
    //stop music forever
     fun stopMusicForever(){

        isMusicPlaying = false

        if (mediaPlayer != null){
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

    }

}