package com.haoduyoudu.DailyAccounts

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.JsonWriter
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.bugly.crashreport.CrashReport
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.*
import java.util.*
import kotlin.concurrent.thread


class MyApplication:Application() {


    companion object{

        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context

        val Add_pictures_or_video = 1
        val Add_pictures = 2
        val Add_videos = 3

        var firstLoadaboutBg = true

        const val SHIELD_SHARE_NOTES_ACTON = false
        const val SHIELD_PASSWORD_ACTON = true
        const val SHIELD_SHARE_ACTON = true

        const val SET_PASSWORD = 1
        const val RESET_PASSWORD = 2
        const val INPUT_PASSWORD = 3
        const val CLOSE_PASSWORD = 4
        const val MAX_PASSWORD_ERROR_TIMES = 5
        const val DIGIT_OF_PASSWORD = 3
        const val FORGET_PASSWORD_TIME = 2*24*60*60*1000L
        const val LOCK_TIME = 1000*60*2L // ten s
        const val MAX_RECORD_COUNT = 6
        const val MAX_IMAGE_OR_VIDEO_COUNT = 3
        const val MAX_STICKER_COUNT = 9
        const val COUNT_OF_DAYS_OF_COLOREGG_TO_APPEAR = 21

        var nowbackground = "weather"

        var needupdata:Boolean? = null

        val filePath = "/data/data/com.haoduyoudu.DailyAccounts/" + "setting.data"

        var Appsetting = HashMap<String,Boolean>()

        var AllPermissionsOK = false

        val Mapoftime = mapOf<Int,String>(
            1 to "早上好",
            2 to "中午好",
            3 to "下午好",
            4 to "晚上好",
            5 to "夜深了"
        )
        val Mapofbackground = mapOf<String,Int>(
            "weather" to R.mipmap.bg_clear_day,
            "city" to R.mipmap.city,
            "grassland" to R.mipmap.grassland,
            "wave" to R.mipmap.wave,
            "youyu" to R.mipmap.q_youyu
        )

        val weatherCodeToString = mapOf<Int,String>(
            0 to "qing",
            1 to "qing",
            2 to "yun",
            3 to "yu",
            4 to "yu",
            5 to "bingbao",
            6 to "xue",
            7 to "yu",
            8 to "yu",
            9 to "yu",
            10 to "yu",
            11 to "yu",
            12 to "yu",
            13 to "xue",
            14 to "xue",
            15 to "xue",
            16 to "wu",
            17 to "xue",
            18 to "wu",
            19 to "yu",
            20 to "shachen",
            21 to "yu",
            22 to "yu",
            23 to "yu",
            24 to "yu",
            25 to "yu",
            26 to "xue",
            27 to "xue",
            28 to "xue",
            29 to "yin",
            30 to "shachen",
            31 to "shachen",
            32 to "wu"
        )

        val Mapofweather = mapOf<String,Int>(
            "xue" to R.mipmap.bg_snow, //雪
            "lei" to R.mipmap.bg_rain, //雷
            "wu" to R.mipmap.bg_fog,   //雾
            "bingbao" to R.mipmap.bg_snow, //冰雹
            "yun" to R.mipmap.bg_cloudy,  //多云
            "yu" to R.mipmap.bg_rain, //雨
            "yin" to R.mipmap.bg_yin, //阴
            "qing" to R.mipmap.bg_clear_day, //晴
            "shachen" to R.mipmap.bg_dusd_storm //沙尘
        )

        val Maooflittleweatherimg = mapOf<String,Int>(
            "xue" to R.mipmap.xue,
            "lei" to R.mipmap.lei,
            "wu" to R.mipmap.wu,
            "bingbao" to R.mipmap.xue,
            "yun" to R.mipmap.yun,
            "yu" to R.mipmap.yu,
            "yin" to R.mipmap.yin,
            "qing" to R.mipmap.qing,
            "shachen" to R.mipmap.shachen
        )

        val weathertosay = mapOf<String,String>(
            "xue" to "小雪花，飘呀飘",
            "lei" to "打雷啦",
            "wu" to "空山不见人",
            "bingbao" to "冰雹，注意防护",
            "yun" to "多云",
            "yu" to "小雨点翩翩起舞",
            "yin" to "太阳在后面隐现",
            "qing" to "蓝蓝的天空",
            "shachen" to "沙尘暴"
        )

        val timetolittleimg = mapOf<Int,Int>(
            1 to R.mipmap.bg_lmorning,
            2 to R.mipmap.bg_lmorning,
            3 to R.mipmap.bg_lafternoon,
            4 to R.mipmap.bg_levening,
            5 to R.mipmap.bg_lstar
        )

        val Say = mapOf<Int,Map<Int,String>>(
            1 to mapOf(   //早上
                1 to "你若盛开，清风自来",
                2 to "清晨第一缕阳光，唤醒沉睡的心房",
                3 to "早上再忙，也要记得吃早饭"
            ),
            2 to mapOf(    //中午
                1 to "好好吃饭用心生活，比什么都幸福",
                2 to "中午好，干饭人，今天也要好好吃饭"
            ),
            3 to mapOf(    //下午
                1 to "下午到了，\n饮茶先啦!",
                2 to "忙了一上午，小睡一会吧"
            ),
            4 to mapOf(   //晚上
                1 to "道一声晚安，许一弧月色",
                2 to "星辰陪伴你梦入眠"
            ),
            5 to mapOf(    //夜深
                1 to "睡意随风起，风止意难平",
                2 to "夜已深，风已静，睡意绵绵缠我心",
                3 to "休息是为了明天能走得更远"
            )
        )


        var Activitys = HashMap<String, Context>()
        var newwrite = false
        var weather:String? = null
        var tem:Int? = null

        var nuberToTextColor = mapOf<Int,Int>(
            1 to R.color.TC_1,
            2 to R.color.TC_2,
            3 to R.color.TC_3,
            4 to R.color.TC_4,
            5 to R.color.TC_5,
            6 to R.color.TC_6,
            7 to R.color.TC_7,
            8 to R.color.TC_8,
            9 to R.color.TC_9,
            10 to R.color.TC_10,
            11 to R.color.TC_11,
            12 to R.color.TC_12
        )

        var idToMoodNumber = mapOf<Int,Int>(
            R.id.radiobutton1 to 1,
            R.id.radiobutton2 to 2,
            R.id.radiobutton3 to 3,
            R.id.radiobutton4 to 4,
            R.id.radiobutton5 to 5,
            R.id.radiobutton6 to 6,
            R.id.radiobutton7 to 7

        )
        var NumberToMoodImage = mapOf<Int,Int>(
            1 to R.mipmap.ishappy,
            2 to R.mipmap.isangry,
            3 to R.mipmap.isnothappy,
            4 to R.mipmap.ispingfan,
            5 to R.mipmap.iswoozyface,
            6 to R.mipmap.isfuck,
            7 to R.mipmap.issweet
        )
        var NumberToMoodSay = mapOf<Int,String>(
            1 to "嗨皮的一天～",
            2 to "气死我了，哼！",
            3 to "宝宝不开心！",
            4 to "平凡的一天～",
            5 to "累趴了～",
            6 to "人生巅峰AwA",
            7 to "你爱我，我爱你"
        )
        val NuberToTemplate = mapOf<Int,Int>(
            1 to R.mipmap.moren,
            2 to R.mipmap.yekong,
            3 to R.mipmap.fenhong,
            4 to R.mipmap.lvyou,
            5 to R.mipmap.dujia,
            6 to R.mipmap.jimo,
            7 to R.mipmap.meishi,
            8 to R.mipmap.fenhongchengbao,
            9 to R.mipmap.xuexi,
            10 to R.mipmap.sikao,
            11 to R.mipmap.jihe,
            12 to R.mipmap.harrypotter,
            13 to R.mipmap.yueye,
            14 to R.mipmap.christmas,
            15 to R.mipmap.newyear,
            16 to R.mipmap.springgirl,
            17 to R.mipmap.springflower,
            18 to R.mipmap.richu,
            19 to R.mipmap.pink,
            20 to R.mipmap.nightbus,
            21 to R.mipmap.jimucaihong,
            22 to R.mipmap.hunian,
            23 to R.mipmap.happybirthday,
            24 to R.mipmap.banana
        )
        fun InitLoad(){
            thread {
                var LoadassestOK = false
                try{
                    if(!File(filePath).exists())
                        DeleteFileUtil.delete("/data/data/com.haoduyoudu.DailyAccounts/assest/")
                    LoadassestOK = Loadassest()  //预加载assests
                }catch (e:Exception){
                    e.printStackTrace()
                }


                try {
                    if(!File(filePath).exists()){
                        needupdata = true
                        setjson(sound = true,first = true,firstloadOK = LoadassestOK)
                    }else{

                        var date:Int? = null
                        var uptime:Int? = null
                        val jsontext = FileUtils.readTxtFile(filePath)
                        val jsonObject = JSONObject(jsontext)


                        date = jsonObject.getString("date").toInt()
                        uptime = jsonObject.getString("uptime").toInt()
                        Appsetting["sound"] = jsonObject.getString("sound") == "on"
                        weather = jsonObject.getString("weather")
                        tem = jsonObject.getString("tem").toInt()
                        if (jsonObject.getString("ver")!=getVer(context)){
                            DeleteFileUtil.delete("/data/data/com.haoduyoudu.DailyAccounts/assest/")
                            setjson(sound = Appsetting["sound"]!!?:true,first = true, firstloadOK = Loadassest())
                        }
                        if(!jsonObject.getBoolean("firstloadOK")){
                            DeleteFileUtil.delete("/data/data/com.haoduyoudu.DailyAccounts/assest/")
                            setjson(first = false,firstloadOK = Loadassest())
                        }
                        try{
                            val back:String? = jsonObject.getString("background")
                            if(back != null){
                                nowbackground = back
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }


                        if((date != getday()) or (gettime() != uptime)){   //updata
                            setjson(sound = Appsetting["sound"]!!?:true,first = true)
                            needupdata = true
                        }else{
                            needupdata = false
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    try{
                        setjson(sound = Appsetting["sound"]!!?:true,first = true)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
        private fun parseJSONWithJSONObject(jsonData:String){
            /*
            try {
                val jsonObject = JSONObject(jsonData)
                val weathers = jsonObject.getString("wea_img")
                weather = weathers
                val tems = jsonObject.getString("tem").toInt()
                tem = tems
            }catch (e: Exception){
                e.printStackTrace()
            }
            */
            val gson = Gson()
            val typeOf = object : TypeToken<WeatherData>() {}.type
            val mWeather = gson.fromJson<WeatherData>(jsonData, typeOf)
            val weathercode = stringToInt(mWeather.data[0].weather.weathercode)
            weather = weatherCodeToString[weathercode]
            tem = stringToInt(mWeather.data[0].weather.temp)

        }

        fun gettime():Int{
            val calendars = Calendar.getInstance()
            calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"))
            val hours:Int = calendars.get(Calendar.HOUR_OF_DAY)
            var times:Int
            if (0<=hours && hours<=10) times = 1
            else if (11<=hours && 12>=hours) times = 2
            else if (13<=hours && 18>=hours) times = 3
            else if (19<=hours && 21>=hours) times = 4
            else if (22<=hours && 23>=hours) times = 5
            else times = 6
            return times
        }
        private fun getday():Int{
            val calendars = Calendar.getInstance()
            calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"))
            val day:Int = calendars.get(Calendar.DATE)
            return day
        }
        fun setjson(sound:Boolean = true,first:Boolean = false,firstloadOK:Boolean = true){
            thread {
                if (first) {getweather()}
                Thread.sleep(2800)
                try {
                    val fileOutputStream = FileOutputStream(filePath);
                    //开始写JSON数据
                    val jsonWriter = JsonWriter(
                        OutputStreamWriter(
                            fileOutputStream, "UTF-8")
                    );
                    jsonWriter.beginObject();
                    jsonWriter.name("date").value(getday().toString())
                    jsonWriter.name("uptime").value(gettime().toString());
                    jsonWriter.name("sound").value(if(sound) "on" else "off");
                    jsonWriter.name("weather").value(weather?:"null");
                    jsonWriter.name("tem").value(tem.toString())
                    jsonWriter.name("ver").value(getVer(context))
                    jsonWriter.name("firstloadOK").value(firstloadOK)
                    jsonWriter.name("background").value(nowbackground)
                    jsonWriter.endObject();
                    Log.d("json","JSON数据写入完毕！");
                    jsonWriter.close();
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }

        private fun getweather(){
            //https://help.bj.cn/Weathera/20200303/06AC9B964A094C6BBE66BDC3A32E6E9A.html
            //https://www.tianqiapi.com/api/?version=v51&appid=1001&appsecret=1046
            thread{
                try{
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://ip.help.bj.cn/")
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent",getUserAgent())
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

        fun copyAssets(context:Context,FileName:String,dir:String,assestpath:String):String{


            val dir = File(dir);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }

            val file = File(dir, FileName);
            var inputStream:InputStream? = null
            var outputStream:OutputStream? = null

            //通过IO流的方式，将assets目录下的数据库文件，写入到SD卡中。
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    inputStream = context.getClassLoader().getResourceAsStream(assestpath + FileName);
                    outputStream = FileOutputStream(file);
                    val buffer = ByteArray(1024)
                    var len:Int;
                    while ((inputStream.read(buffer).also { len = it }) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    Log.d("copyAssests",file.absolutePath)
                } catch (e:IOException) {
                    e.printStackTrace();
                } finally {
                    outputStream?.flush();
                    outputStream?.close();
                    inputStream?.close();
                }
            }
            return file.getPath();
        }

        fun Loadassest():Boolean{
            var OK = false
            try{
                while(!AllPermissionsOK){}
                val rpath = "/data/data/com.haoduyoudu.DailyAccounts/"
                if(!File(rpath+"assest/").exists()) {
                    FileUtils.makeRootDirectory(rpath + "assest/")
                }
                if (!File(rpath+"assest/mood/").exists()){
                    FileUtils.makeRootDirectory(rpath + "assest/" + "mood/")
                    for (i in 1..59) {
                        copyAssets(context, i.toString() + ".png","/data/data/com.haoduyoudu.DailyAccounts/assest/mood/","assets/mood/")
                    }
                }
                if (!File(rpath+"assest/sticker/").exists()){
                    FileUtils.makeRootDirectory(rpath + "assest/" + "sticker/")
                    for (i in context.assets.list("sticker")!!) {
                        copyAssets(context, i.toString(),"/data/data/com.haoduyoudu.DailyAccounts/assest/sticker/","assets/sticker/")
                    }
                }

                OK = true
            }catch (e:Exception){
                e.printStackTrace()

            }
            return OK
        }
        fun getVer(ct:Context):String = context.getPackageManager().getPackageInfo(ct.getPackageName(), 0).versionName

        private fun getUserAgent(): String {
            var userAgent = ""
            val sb = StringBuffer()
            userAgent = System.getProperty("http.agent") as String
            var i = 0
            val length = userAgent.length
            while (i < length) {
                val c = userAgent[i]
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", c.code))
                } else {
                    sb.append(c)
                }
                i++
            }
            Log.v("User-Agent", "User-Agent: $sb")
            return sb.toString()
        }

        private fun stringToInt(uri:String):Int{
            var result = 0
            var intStart = -1
            for(i in uri.indices){
                if(uri[i] in '0'..'9'){
                    result += uri[i]-'0'
                    result *= 10
                    if(intStart == -1) intStart = i
                }
            }
            if(intStart!=0) if(uri[intStart-1]=='-') return -(result/10)
            return result/10
        }
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("Appstart","ok!")

        context = applicationContext

        CrashReport.initCrashReport(getApplicationContext(), "3d71114e10", false);
    }

}