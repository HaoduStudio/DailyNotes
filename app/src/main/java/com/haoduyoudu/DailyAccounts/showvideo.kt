package com.haoduyoudu.DailyAccounts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_showvideo.*

class showvideo : AppCompatActivity() {

    lateinit var ringReceiver: RINGReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showvideo)
        val path = intent.getStringExtra("imagepath")
        val videoname = intent.getStringExtra("imagename")
        VideoView.setVideoPath(path)
        try {
            if(!VideoView.isPlaying){
                VideoView.start()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("com.xtc.alarmclock.action.ALARM_VIEW_SHOWING")
        intentFilter.addAction("com.xtc.videochat.start")
        intentFilter.addAction("android.intent.action.PHONE_STATE")
        ringReceiver = RINGReceiver()
        registerReceiver(ringReceiver,intentFilter)
    }


    override fun onDestroy() {
        super.onDestroy()
        VideoView.suspend()
        unregisterReceiver(ringReceiver)
    }

    override fun onStop() {
        super.onStop()
        VideoView.pause()
    }

    override fun onRestart() {
        super.onRestart()
        VideoView.start()
    }

    inner class RINGReceiver : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("colck","哥哥电话来啦！")
            finish()
        }
    }
}
