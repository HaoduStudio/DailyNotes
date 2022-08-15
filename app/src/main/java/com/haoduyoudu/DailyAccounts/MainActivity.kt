package com.haoduyoudu.DailyAccounts

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.Mapofweather
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.needupdata
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.weather
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.concurrent.thread
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import android.content.pm.PackageManager
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    lateinit var adapter: TextviewButtonListAdapter
    private val textviewbuttonList = ArrayList<TextviewButtonList>()
    var repeattouch = false
    var firstLoad = true
    var firststart = false
    var needtoinputpassword = false

    lateinit var lastitem:TextviewButtonList
    lateinit var lastitemview:View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.activity_main)

        thread {
            if(File("/data/data/com.haoduyoudu.DailyAccounts/"+"password.data").exists())
                needtoinputpassword = true
            if(needupdata?:true)
                Thread.sleep(2000)
            else
                Thread.sleep(500)
            if(!(hasPermission(this,"android.permission.WRITE_EXTERNAL_STORAGE")
                && hasPermission(this,"android.permission.READ_EXTERNAL_STORAGE")
                && hasPermission(this,"android.permission.RECORD_AUDIO"))){
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO),1)
                //Manifest.permission.RECORD_AUDIO
                try {
                    while(!firststart){Thread.sleep(50)}
                }catch (e:Exception){
                    Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    finish()
                }
            }else{
                firststart = true
                MyApplication.AllPermissionsOK = true
                Log.d("Main1","else master,all oermissions OK")
            }
            Log.d("Main1","AllPermissionsOK ${MyApplication.AllPermissionsOK}")
            Log.d("Main1","firststart ${firststart}")
            if(MyApplication.AllPermissionsOK){
                MyApplication.InitLoad()
            }
            try{
                adapter = TextviewButtonListAdapter(this, R.layout.tewtviewbuttonlistwithnotes_item, textviewbuttonList)
                initTextviewButtonList()
                refreshTBL()
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                    listView.adapter = adapter
                }

            }catch (e:Exception){
                e.printStackTrace()
            }
            Thread.sleep(300)
            try {
                val pathofdailyaccounts = "/sdcard/Android/data/com.haoduyoudu.DailyAccounts/"
                var Filenamesofdailyaccounts = GFN(pathofdailyaccounts)
                if(Filenamesofdailyaccounts.size != 0 && weather != null && needupdata == true){
                    startActivityForResult(Intent(this,SayHello::class.java),3)
                }else if(Filenamesofdailyaccounts.size != 0 && weather == null && needupdata == true){
                    thread {
                        var outtime = 0
                        while (weather == null && outtime<=1000){
                            Thread.sleep(100)
                            outtime+=100
                        }
                        if (weather != null) {
                            startActivityForResult(Intent(this, SayHello::class.java), 3)
                            needupdata = false
                        }else{
                            runOnUiThread {
                                if(needupdata == true)
                                    Toast.makeText(this,MyApplication.Mapoftime[MyApplication.gettime()],Toast.LENGTH_SHORT).show()
                                showUseTip()
                            }
                        }
                        if(outtime>1000)
                            LoadmainUI()
                    }
                }else{
                    LoadmainUI()
                    showUseTip()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        MyApplication.Activitys.put("MainActivity",this)
        swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeRefresh.setOnRefreshListener {
            refreshfromsR(adapter)
        }

        menu.addClickScale()
        write.addClickScale()
        moodcalendar.addClickScale()
        findda.addClickScale()
        pifu.addClickScale()
        about.addClickScale()
        setsafety.addClickScale()
        menu.setOnClickListener {
            mDrawerLayout.openDrawer(GravityCompat.END)
        }
        write.setOnClickListener {
            MyApplication.newwrite = true
            val intent = Intent(this,DailyAccounts::class.java)
            startActivity(intent)
            mDrawerLayout.closeDrawer(GravityCompat.END)
        }
        moodcalendar.setOnClickListener {
            val intent = Intent(this,Moodcalendar::class.java)
            startActivity(intent)
            mDrawerLayout.closeDrawer(GravityCompat.END)
        }
        findda.setOnClickListener {
            val intent = Intent(this,find::class.java)
            startActivity(intent)
            mDrawerLayout.closeDrawer(GravityCompat.END)
        }
        pifu.setOnClickListener {
            val intent = Intent(this,cel_background::class.java)
            startActivity(intent)
            mDrawerLayout.closeDrawer(GravityCompat.END)
        }
        setsafety.setOnClickListener {
            val intent = Intent(this,Settingpage::class.java)
            startActivity(intent)
            mDrawerLayout.closeDrawer(GravityCompat.END)
        }
        about.setOnClickListener {
            val intent = Intent(this,aboutsoftware::class.java)
            startActivity(intent)
            mDrawerLayout.closeDrawer(GravityCompat.END)
        }

        if(MyApplication.SHIELD_PASSWORD_ACTON){
            setsafety.visibility=View.GONE
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0,0,0,0)
            setsafety.layoutParams = lp
        }

        listView.setOnItemClickListener { _, view, position, _ ->
            
            if(!repeattouch){
                repeattouch = true
                val textviewobj = textviewbuttonList[position]

                val image:ImageView = view.findViewById(R.id.ListImage)

                val intent = Intent(this,showdailyaccount::class.java)
                intent.putExtra("path",textviewobj.path)
                intent.putExtra("date",textviewobj.name)
                intent.putExtra("index",textviewobj.index)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try{
                        image.transitionName = "Image"
                        val options = ActivityOptions.makeSceneTransitionAnimation(this, image,"Image")
                        startActivity(intent,options.toBundle())
                    }catch (e:Exception){
                        e.printStackTrace()
                        startActivity(intent)
                    }
                }else{
                    startActivity(intent)
                }
                repeattouch = false
            }
        }
        listView.setOnItemLongClickListener { _, view, position, _ ->
            val textviewobj = textviewbuttonList[position]
            DeleteFileUtil.delete(File(cacheDir.absolutePath,"shot.jpg").absolutePath)
            FileUtils.savebitmap(rsBlur(this,viewConversionBitmap(main_background)!!,8),cacheDir.absolutePath,"shot.jpg",80)
            lastitem = textviewobj
            lastitemview = view
            if(MyApplication.SHIELD_SHARE_NOTES_ACTON) startActivityForResult(Intent(this,more_ac2::class.java),4)
            else startActivityForResult(Intent(this,more_ac::class.java),4)
            true
        }
    }
    override fun onResume() {
        super.onResume()
        print("onRestart()")
        try{
            MyApplication.Activitys["MainActivity"] = this
            if(MyApplication.newwrite){
                print("ref")
                initTextviewButtonList()
                refreshTBL()
                adapter.notifyDataSetChanged()
                MyApplication.newwrite = false
            }
        }catch(e:Exception){
            e.printStackTrace()
        }

    }
    private fun refreshTBL(){
        val pathofdailyaccounts = "/sdcard/Android/data/com.haoduyoudu.DailyAccounts/"
        var Filenamesofdailyaccounts = GFN(pathofdailyaccounts)
        if (Filenamesofdailyaccounts.size != 0) {
            if(firstLoad){
                runOnUiThread {
                    try{
                        Glide.with(this)
                            .load(R.mipmap.black)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(weather_img)
                        loadbackground()
                        Log.d("MainActivity","刷新 天气图片")
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }else{
                loadbackground()
            }
            listView.setBackgroundResource(0)
            for (i in 0..Filenamesofdailyaccounts.size-1) {
                try {
                    val moodtext = FileUtils.readTxtFile(pathofdailyaccounts+Filenamesofdailyaccounts[i]+"/"+"mood.txt")
                    val moodsplit = moodtext.split("$[%|!|%]$")
                    var imageId: Int
                    if (moodsplit.size == 2)
                        imageId = moodsplit[0].toInt()
                    else
                        imageId = MyApplication.NumberToMoodImage[moodtext.toInt()] ?: R.mipmap.isnoneface

                    val name = (Filenamesofdailyaccounts[i].substring(0,4) + "-" +  //87
                            Filenamesofdailyaccounts[i].substring(4,6) +
                            "-" +Filenamesofdailyaccounts[i].substring(6,8))

                    val mdate = FileUtils.readTxtFile(pathofdailyaccounts + Filenamesofdailyaccounts[i]+ "/week.txt")
                    val weekDays = mapOf<String,Int>(
                        "Sun" to 0,
                        "Mon" to 1,
                        "Tue" to 2,
                        "Wed" to 3,
                        "Thu" to 4,
                        "Fri" to 5,
                        "Sat" to 6
                    )
                    val weekDays2: Array<String> =
                        arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

                    val mdate2 = getDate(Filenamesofdailyaccounts[i].substring(0,4).toInt(),
                        Filenamesofdailyaccounts[i].substring(4,6).toInt(),
                        Filenamesofdailyaccounts[i].substring(6,8).toInt())

                    if(mdate2 != weekDays[mdate.toString()]){
                        Log.d("MainAvtivity","${mdate2}  ${weekDays[mdate.toString()]}")
                        DeleteFileUtil.delete(pathofdailyaccounts + Filenamesofdailyaccounts[i]+ "/week.txt")
                        FileUtils.writeTxtToFile(weekDays2[mdate2],pathofdailyaccounts + Filenamesofdailyaccounts[i] + "/", "week.txt")
                        Log.e("MainActivity","重新更正日期")
                    }

                    textviewbuttonList.add(
                        TextviewButtonList(
                            name,
                            imageId,
                            pathofdailyaccounts+Filenamesofdailyaccounts[i]+"/",
                            "DailyAccounts",
                            notes = weekDays2[mdate2],
                            index = i))
                    Log.d("xxx",FileUtils.readTxtFile(pathofdailyaccounts+ Filenamesofdailyaccounts[i]+ "/week.txt"))
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            if(Filenamesofdailyaccounts.size == 0)
                runOnUiThread {
                    try{
                        listView.setBackgroundResource(R.mipmap.kong)
                        Glide.with(this)
                            .load(R.mipmap.black)
                            .into(weather_img)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

        }else{
            runOnUiThread {
                try{
                    listView.setBackgroundResource(R.mipmap.kong)
                    Glide.with(this)
                        .load(R.mipmap.black)
                        .into(weather_img)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
        try{
            if (!firstLoad)
                showUseTip()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun refreshfromsR(adapter: TextviewButtonListAdapter){
        thread {
            Thread.sleep(2000)
            runOnUiThread{
                try{
                    initTextviewButtonList()
                    refreshTBL()
                    adapter.notifyDataSetChanged()
                    swipeRefresh.isRefreshing = false
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when(requestCode){
                3 -> if(resultCode == RESULT_OK){
                    needupdata = false
                    LoadmainUI()
                    showUseTip()
                }
                4 -> if(resultCode == RESULT_OK){
                    if(data != null){
                        when(data.getStringExtra("type")){
                            "edit" -> {
                                val image:ImageView = lastitemview.findViewById(R.id.ListImage)

                                val intent = Intent(this,showdailyaccount::class.java)
                                intent.putExtra("path",lastitem.path)
                                intent.putExtra("date",lastitem.name)
                                intent.putExtra("index",lastitem.index)
                                intent.putExtra("rewrite",true)
                                startActivity(intent)
                            }
                            "del" -> {
                                DeleteFileUtil.delete(lastitem.path)
                                initTextviewButtonList()
                                refreshTBL()
                                adapter.notifyDataSetChanged()
                                Toast.makeText(this,getString(R.string.del_ok),Toast.LENGTH_SHORT).show()
                            }
                            "share" -> {
                                val image:ImageView = lastitemview.findViewById(R.id.ListImage)

                                val intent = Intent(this,showdailyaccount::class.java)
                                intent.putExtra("path",lastitem.path)
                                intent.putExtra("date",lastitem.name)
                                intent.putExtra("index",lastitem.index)
                                intent.putExtra("ac","shot")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    try{
                                        image.transitionName = "Image"
                                        val options = ActivityOptions.makeSceneTransitionAnimation(this, image,"Image")
                                        startActivity(intent,options.toBundle())
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                        startActivity(intent)
                                    }
                                }else{
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    private fun initTextviewButtonList(){
        textviewbuttonList.clear()
    }
    fun GFN(dirpathx:String):MutableList<String>{
        val fileNames: MutableList<String> = mutableListOf()
        //在该目录下走一圈，得到文件目录树结构
        val fileTree: FileTreeWalk = File(dirpathx).walk()
        fileTree.maxDepth(1) //需遍历的目录层次为1，即无须检查子目录
            .filter { it.isDirectory && it.name != "assest"} //只挑选文件，不处理文件夹
            //.filter { it.extension in listOf("m4a","mp3") }
            .forEach { fileNames.add(it.name) }//循环 处理符合条件的文件
        if(fileNames.size!=0){
            fileNames.removeAt(0)
            fileNames.sort()
            fileNames.reverse()
        }
        return fileNames
    }

    fun crossfadeToContentView(contentView: View) {

        // 设置内容contentView为0%的不透明度，但是状态为“可见”，
        // 因此在动画过程中是一直可见的（但是为全透明）。
        contentView.setAlpha(0f);
        contentView.setVisibility(View.VISIBLE);

        // 开始动画内容contentView到100%的不透明度，然后清除所有设置在View上的动画监听器。
        contentView.animate().alpha(1f).setDuration(1000)
            .setListener(null);

        // 加载progressView开始动画逐渐变为0%的不透明度，
        // 动画结束后，设置可见性为GONE（消失）作为一个优化步骤
        // （它将不再参与布局的传递等过程）
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MyApplication.Activitys.put("Hello",this)
        when(requestCode){
            1 -> {
                for(i in permissions){
                    Log.d("权限",i+"\n")
                }
                for(i in grantResults){
                    Log.d("权限",i.toString()+"\n")
                }
                if(grantResults.size == 3){
                    if(grantResults[0] == 0 && grantResults[1] == 0 && grantResults[2] == 0){    //Bug #51
                        if(!firststart){
                            firststart = true
                            MyApplication.AllPermissionsOK = true
                        }
                    }else{
                        runOnUiThread {
                            setTheme(R.style.AppTheme)
                            Toast.makeText(this,getString(R.string.main_premissions_tips), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }else{
                    runOnUiThread {
                        setTheme(R.style.AppTheme)
                        Toast.makeText(this,getString(R.string.system_error), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

            }
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (!firstLoad) {
            if (needtoinputpassword && !MyApplication.SHIELD_PASSWORD_ACTON){
                startActivity(Intent(this,inputpassword::class.java).apply { putExtra("type",MyApplication.INPUT_PASSWORD) })
                thread {
                    Thread.sleep(500)
                    needtoinputpassword = false
                }
                false
            }else{
                super.dispatchTouchEvent(ev)
            }
        } else {
            false
        }
    }

    fun LoadmainUI(){
        if(firstLoad){
            runOnUiThread {   //worried
                try{
                    crossfadeToContentView(listView)
                    crossfadeToContentView(Title)
                    menu.visibility = View.VISIBLE
                    menuyinying.visibility = View.VISIBLE
                    main_background.setBackgroundColor(getResources().getColor(R.color.black))
                }catch (e:Exception){
                    e.printStackTrace()
                }
                firstLoad = false
            }
        }
    }
    private fun rsBlur(context: Context, source: Bitmap, radius: Int): Bitmap {
        val renderScript = RenderScript.create(context)
        Log.i("blur", "scale size:" + source.width + "*" + source.height)
        val input = Allocation.createFromBitmap(renderScript, source)
        val output = Allocation.createTyped(renderScript, input.type)
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptIntrinsicBlur.setInput(input)
        scriptIntrinsicBlur.setRadius(radius.toFloat())
        scriptIntrinsicBlur.forEach(output)
        output.copyTo(source)
        renderScript.destroy()
        return source
    }
    fun viewConversionBitmap(v: View,config:Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
        val w = v.width
        val h = v.height
        val bmp = Bitmap.createBitmap(w, h, config)
        val c = Canvas(bmp)
        /** 如果不设置canvas画布为白色，则生成透明  */
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp
    }
    fun loadbackground(){
        thread {
            if(MyApplication.nowbackground == "weather"){
                while(weather==null){Thread.sleep(100)}
                val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                runOnUiThread {
                    try{
                        if(Mapofweather[weather] != null){
                            Glide.with(this)
                                .load(Mapofweather[weather])
                                .transition(withCrossFade(factory))
                                .into(weather_img)
                        }else{
                            listView.setBackgroundResource(R.mipmap.black)
                            Glide.with(this)
                                .load(R.mipmap.black)
                                .into(weather_img)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }else{
                runOnUiThread {
                    if(MyApplication.nowbackground != "wave") {
                        try {
                            Glide.with(this)
                                .load(MyApplication.Mapofbackground[MyApplication.nowbackground])
                                .into(weather_img)
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }else {
                        when (MyApplication.gettime()) {
                            1, 2, 3 -> {
                                try{
                                    Glide.with(this)
                                        .load(R.mipmap.wave)
                                        .into(weather_img)
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }
                            4, 5, 6 -> {
                                try {
                                    Glide.with(this)
                                        .load(R.mipmap.wave2)
                                        .into(weather_img)
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }

                        }
                    }
                }
            }
        }
    }
    fun hasPermission(context: Context, permission: String?): Boolean {
        return context.checkCallingOrSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
    }
    private fun getDate(ly: Int, lm: Int, ld: Int):Int{ //栓Q XC I Love You ～～
        val ly2 = if(lm < 3) ly-1 else ly
        val c = ly2.toString().subSequence(0,2).toString().toInt()
        val y = ly2.toString().subSequence(2,4).toString().toInt()
        val m = if(lm < 3) 12+lm else lm
        val d = ld
        return (y+(y/4)+(c/4)-2*c+(26*(m+1)/10)+d-1)%7
    }
    private fun showUseTip(){
        if(GFN("/sdcard/Android/data/com.haoduyoudu.DailyAccounts/").size != 0
            && (!File(filesDir.path,"NOTNEW.dt").exists())){
            thread {
                Thread.sleep(800)
                startActivity(Intent(this,guideInterface::class.java))
            }
            Log.d("MainActivity","showUseTip")
        }
    }
}