package com.haoduyoudu.DailyAccounts

import android.media.MediaPlayer

class PlaymediaUtils {
    val mp = MediaPlayer()
    fun play(path:String){
        initMP(path)
        if(!mp.isPlaying){
            mp.start()
        }
    }
    fun pause(){
        if(mp.isPlaying)
            mp.pause()
    }
    fun stop(){
        try {
            mp.stop()
            mp.reset()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun initMP(path:String){
        mp.reset()
        mp.setDataSource(path)
        mp.prepare()
    }
    fun isPlaying():Boolean {
        try {
            return mp.isPlaying()
        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
    }
    fun clean(){
        mp.release()
    }
    fun getTime(path:String):Long{
        initMP(path)
        return mp.getDuration().toLong()
    }
}