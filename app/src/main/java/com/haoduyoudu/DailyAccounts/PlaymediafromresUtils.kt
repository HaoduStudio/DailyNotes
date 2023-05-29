package com.haoduyoudu.DailyAccounts

import android.app.Activity
import android.media.MediaPlayer
import kotlin.concurrent.thread

class PlaymediafromresUtils(musicid:Int,connect:Activity) {
    val mp = MediaPlayer.create(connect,musicid)
    fun play(){
        if(!mp.isPlaying){
            mp.start()
        }
    }
    fun pause(){
        if(mp.isPlaying)
            mp.pause()
    }
    fun strat(){
        if(!mp.isPlaying){
            mp.start()
        }
    }
    fun stop(){
        if(mp.isPlaying){
            mp.reset()
        }
    }
    fun isPlaying():Boolean {
        return mp.isPlaying
    }
    fun setLooping(){
        mp.setLooping(true)
    }
    fun release(){
        if(mp!=null){
            mp.release()
        }
    }
    fun setfadeout(time:Int){
        try {
            thread {
                while(mp.getDuration()-mp.getCurrentPosition() <= time){Thread.sleep(100) }
                for(i in 10 downTo 1){
                    Thread.sleep((time/10)as Long)
                    mp.setVolume((i/10) as Float,(i/10) as Float)
                }
                stop()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}