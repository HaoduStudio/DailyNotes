package com.haoduyoudu.DailyAccounts

import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

fun View.addClickScale(scale: Float = 0.9f, duration: Long = 150) {
    this.setOnTouchListener { _, event ->
        try{
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.animate().scaleX(scale).scaleY(scale).setDuration(duration).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    this.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
                }
            }
            if(event.action == MotionEvent.ACTION_UP){
                val vibrator = MyApplication.context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(34) //单位是ms
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        // 点击事件处理，交给View自身
        this.onTouchEvent(event)
    }
}

fun View.addScale(startscale: Float = 0.1f, endscale: Float = 1.0f,duration: Long = 300){
    try {
        this.animate().scaleX(startscale).scaleY(startscale).setDuration(0).start()
        this.animate().scaleX(endscale).scaleY(endscale).setDuration(duration).start()
    }catch (e:Exception){
        e.printStackTrace()
    }
}
fun View.setScale(scale: Float = 0.1f){
    try {
        this.animate().scaleX(scale).scaleY(scale).setDuration(0).start()
    }catch (e:Exception){
        e.printStackTrace()
    }
}

