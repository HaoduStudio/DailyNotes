package com.haoduyoudu.DailyAccounts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_showaudio.*
import kotlin.concurrent.thread

class showaudio : AppCompatActivity() {
    var playmedia = PlaymediaUtils()
    lateinit var ringReceiver: RINGReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showaudio)
        val path = intent.getStringExtra("recordpath")
        val recordname = intent.getStringExtra("recordname")
        name.text = recordname

        try{
            playmedia.play(path.toString())
        }catch (e:Exception){
            Toast.makeText(this,"系统有点忙,或者音频出错了哦～",Toast.LENGTH_SHORT).show()
            finish()
        }


        val intentFilter = IntentFilter()
        intentFilter.addAction("com.xtc.alarmclock.action.ALARM_VIEW_SHOWING")
        intentFilter.addAction("com.xtc.videochat.start")
        intentFilter.addAction("android.intent.action.PHONE_STATE")
        ringReceiver = RINGReceiver()
        registerReceiver(ringReceiver,intentFilter)


        thread {
            try{
                while (playmedia.isPlaying()){
                    Thread.sleep(10)
                }
            }finally {
                runOnUiThread{
                    zhuangtai.text = "播放完成"
                }
                playmedia.stop()
                Thread.sleep(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playmedia.stop()
        playmedia.clean()
        unregisterReceiver(ringReceiver)
    }



    inner class RINGReceiver : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("colck","马牛逼！")
            finish()
        }
    }



}
