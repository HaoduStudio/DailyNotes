package com.haoduyoudu.DailyAccounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_say_hello.*
import java.util.*
import kotlin.concurrent.thread

class SayHello : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_say_hello)

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        try {
            //background_weather.setBackgroundResource(MyApplication.Mapofweather[MyApplication.weather?:"qing"] as Int)
            time_say.setText(MyApplication.Mapoftime[MyApplication.gettime()])  // Good morning
            tem_say.setText(MyApplication.tem.toString()+"˚C"?:"None")

            val calendars = Calendar.getInstance()
            calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"))
            val hours:Int = calendars.get(Calendar.HOUR_OF_DAY)

            Glide.with(this)
                .load(MyApplication.Maooflittleweatherimg[MyApplication.weather])
                .error(R.mipmap.qing)
                .into(weather_img)

            weather_say.setText(MyApplication.weathertosay[MyApplication.weather?:null]?:"Weather gone")

            Glide.with(this)
                .load(MyApplication.timetolittleimg[MyApplication.gettime()])
                .error(MyApplication.timetolittleimg[1]!!)
                .into(Say_img)
            Log.d("Say",MyApplication.Say[MyApplication.gettime()]!![(1..MyApplication.Say[MyApplication.gettime()]!!.size).random()].toString())
            Say.setText(MyApplication.Say[MyApplication.gettime()]!![(1..MyApplication.Say[MyApplication.gettime()]!!.size).random()].toString())
        }catch (e:Exception){
            e.printStackTrace()
        }
        quitfloat.setOnClickListener {
            background_weather.visibility = View.GONE
            val intent = Intent()
            setResult(RESULT_OK,intent)
            finish()
        }
        thread {
            Thread.sleep(5000)
            runOnUiThread {
                background_weather.visibility = View.GONE
            }
            try {
                val intent = Intent()
                setResult(RESULT_OK,intent)
                finish()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        thread {
            Thread.sleep(1200)
            runOnUiThread {
                quitfloat.visibility=View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}