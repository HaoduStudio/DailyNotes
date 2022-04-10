package com.haoduyoudu.DailyAccounts

import android.content.Context
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonWriter
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_cel_background.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*
import kotlin.concurrent.thread

class cel_background : AppCompatActivity() , View.OnClickListener{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cel_background)
        val viewgroups = arrayListOf<View>(item1,item2,item3,item4,item5)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in 0..viewgroups.size-1){
                viewgroups[i].isEnabled = false
                viewgroups[i].addClickScale()
                viewgroups[i].setScale(0.8f)
                viewgroups[i].setOnClickListener(this)
            }
            viewgroups[0].isEnabled = true
            viewgroups[0].setScale(1f)
            MyScrollview.setOnScrollChangeListener(object: View.OnScrollChangeListener {
                override fun onScrollChange(view: View, i: Int, i1: Int, i2: Int, i3: Int) {
                    val scrollBounds = Rect();
                    view.getHitRect(scrollBounds);
                    for(i in 1..viewgroups.size-1)
                        if (viewgroups[i].getLocalVisibleRect(scrollBounds) && viewgroups[i].isEnabled == false) {
                            viewgroups[i].isEnabled = true
                            viewgroups[i].addScale(0.8f,1.0f)
                        }
                }
            })
        };

    }

    override fun onClick(v: View?) {
        val mapofitem = mapOf<View,String>(
            item1 to "weather",
            item2 to "city",
            item3 to "grassland",
            item4 to "wave",
            item5 to "youyu"
        )
        if(v!=null)
            try{
                MyApplication.newwrite = true
                MyApplication.nowbackground = mapofitem[v]!!
                setjson(first = MyApplication.needupdata!!, background = mapofitem[v]!!)
                finish()
            }catch (e:Exception){
                e.printStackTrace()
            }
    }
    private fun parseJSONWithJSONObject(jsonData:String){
        try {
            val jsonObject = JSONObject(jsonData)
            val weathers = jsonObject.getString("wea_img")
            MyApplication.weather = weathers
            val tems = jsonObject.getString("tem").toInt()
            MyApplication.tem = tems
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun gettime():Int{
        val calendars = Calendar.getInstance()
        calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"))
        val hours:Int = calendars.get(Calendar.HOUR_OF_DAY)
        var times:Int
        if (1<=hours && hours<=10) times = 1
        else if (11<=hours && 12>=hours) times = 2
        else if (13<=hours && 18>=hours) times = 3
        else if (19<=hours && 21>=hours) times = 4
        else if (22<=hours && 24>=hours) times = 5
        else times = 6
        return times
    }
    private fun getday():Int{
        val calendars = Calendar.getInstance()
        calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"))
        val day:Int = calendars.get(Calendar.DATE)
        return day
    }
    fun setjson(sound:Boolean = true,first:Boolean = false,firstloadOK:Boolean = true,background:String = MyApplication.nowbackground){
        thread {
            if (first) {getweather()}
            Thread.sleep(2800)
            try {
                val fileOutputStream = FileOutputStream(MyApplication.filePath);
                //开始写JSON数据
                val jsonWriter = JsonWriter(
                    OutputStreamWriter(
                        fileOutputStream, "UTF-8")
                );
                jsonWriter.beginObject();
                jsonWriter.name("date").value(getday().toString())
                jsonWriter.name("uptime").value(gettime().toString());
                jsonWriter.name("sound").value(if(sound) "on" else "off");
                jsonWriter.name("weather").value(MyApplication.weather ?:"null");
                jsonWriter.name("tem").value(MyApplication.tem.toString())
                jsonWriter.name("ver").value(getVer(applicationContext))
                jsonWriter.name("firstloadOK").value(firstloadOK)
                jsonWriter.name("background").value(MyApplication.nowbackground)
                jsonWriter.endObject();
                Log.d("json","JSON数据写入完毕！");
                jsonWriter.close();
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun getweather(){
        thread{
            try{
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://www.tianqiapi.com/api/?version=v51"+"&appid=1001&appsecret=1046")
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                Log.e("weather",responseData.toString())
                if(responseData != null) {
                    //    show weather
                    parseJSONWithJSONObject(responseData)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    fun getVer(ct: Context):String = MyApplication.context.getPackageManager().getPackageInfo(ct.getPackageName(), 0).versionName
}