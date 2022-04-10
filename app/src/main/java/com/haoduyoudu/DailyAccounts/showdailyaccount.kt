package com.haoduyoudu.DailyAccounts

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_showdailyaccount.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import java.io.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.os.Vibrator
import android.view.*
import com.xtc.shareapi.share.communication.SendMessageToXTC
import com.xtc.shareapi.share.interfaces.IResponseCallback
import com.xtc.shareapi.share.manager.ShareMessageManager
import com.xtc.shareapi.share.shareobject.XTCImageObject
import com.xtc.shareapi.share.shareobject.XTCShareMessage
import com.xtc.shareapi.share.manager.XTCCallbackImpl
import com.xtc.shareapi.share.communication.ShowMessageFromXTC

import com.xtc.shareapi.share.communication.BaseResponse
import android.view.ViewGroup
import android.renderscript.ScriptIntrinsicBlur

import android.renderscript.Allocation
import android.renderscript.Element

import android.renderscript.RenderScript
import androidx.core.view.size


class showdailyaccount : AppCompatActivity(), IResponseCallback {

    private val imageorvideoList = ArrayList<TextviewButtonList>()
    private val recordList = ArrayList<TextviewButtonList>()

    lateinit var pathx:String
    lateinit var namex:String

    lateinit var adapterofimageorvideo:TextviewButtonListAdapter
    lateinit var adapterofrecord:TextviewButtonListAdapter
    var indexx:Int = -2
    var isrewrite = false
    lateinit var edithint:String

    var imagetimes:Int = 1
    var videotimes:Int = 1
    var recordtimes:Int = 1

    var more_ac_show = true

    lateinit var xtcCallback:XTCCallbackImpl

    var isonDestroy = false
    var textColor = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = intent.getStringExtra("path")
        val name = intent.getStringExtra("date")
        val index = intent.getIntExtra("index",-2)


        isrewrite = intent.getBooleanExtra("rewrite",false)
        pathx = path.toString()
        namex = name.toString()
        indexx = index.toInt()

        if(isrewrite) setTheme(R.style.DialogActivityTheme)

        setContentView(R.layout.activity_showdailyaccount)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);


        preview.addClickScale()
        if(isrewrite) preview.visibility = View.VISIBLE
        else preview.visibility = View.GONE
        preview.setOnClickListener {
            try {
                thread {
                    DeleteFileUtil.delete(File(cacheDir.absolutePath,"shot.png").absolutePath)
                    runOnUiThread {
                        ifrewrite(false)
                        preview.visibility = View.GONE
                        mStickerLayout.visibility=View.GONE
                    }
                    Thread.sleep(250)
                    runOnUiThread {
                        FileUtils.savebitmap(viewConversionBitmap(f_background),cacheDir.absolutePath,"shot.png",100,Bitmap.CompressFormat.PNG)
                    }
                    runOnUiThread {
                        preview.visibility = View.VISIBLE
                        ifrewrite(true)
                    }
                    startActivityForResult(Intent(this,prewiew_dailyaccount::class.java).putExtra("path",pathx),5)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            moodimage.transitionName = "Image"
        }

        xtcCallback = XTCCallbackImpl()
        xtcCallback.handleIntent(intent, this)
        /**
         * 贴纸类点击事件
         *
         */
        DeleteFileUtil.delete(File(cacheDir,"rewrite.data").absolutePath)
        mStickerLayout.setOnMoveSkListener { x, y ->
            Log.d("Sticker","Move,x ${x.toString()},y ${y.toString()}")
            Log.d("ScrollView","Move,y ${mScrollView.scrollY.toString()}")
            if(y<mScrollView.scrollY+50){
                Log.d("Sticker","MoveUp")
                mScrollView.post(object:Runnable {
                    override fun run() {
                        mScrollView.smoothScrollTo(0,mScrollView.scrollY-10)
                    }
                })
            }else if(y>mScrollView.scrollY+360-50){
                Log.d("Sticker","MoveDown")
                mScrollView.post(object:Runnable {
                    override fun run() {
                        mScrollView.smoothScrollTo(0,mScrollView.scrollY+10)
                    }
                })
            }
        }

        edithint = editsometext.hint.toString()
        Glide.get(this).clearMemory();
        thread {
            Glide.get(this).clearDiskCache();
        }
        mStickerLayout.removeAllSticker()
        if(!isrewrite)
            load_sks()

        uploadbackground()

        val week = FileUtils.readTxtFile(path+"week.txt")



        date.text = name.toString()
        whatweek.text = week.toString()

        uploadmood()

        editsometext.setText(FileUtils.readTxtFile(path+"text.txt"))

        uptextcolor()
        try {
            initimageorvideolist()
            if(imageorvideoList.size != 0){
                for(i in 0..imageorvideoList.size-1){
                    if(imageorvideoList[i].type == "image") imagetimes +=1
                    else if(imageorvideoList[i].type == "video") videotimes +=1
                }
            }
            initrecordlist()
            if(recordList.size != 0){
                for(i in 0..recordList.size-1){
                    if(recordList[i].type == "record") recordtimes +=1
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(this,"手帐出错啦！",Toast.LENGTH_LONG).show()
            try {
                mStickerLayout.visibility = View.GONE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition()
                }else{
                    finish()
                }
            }catch (e:Exception){
                finish()
            }
        }
        ifrewrite(isrewrite)
        imageorvideolist.setOnItemLongClickListener { _, _, position, _ ->
            val textviewobj = imageorvideoList[position]
            if(isrewrite){
                intent = Intent(this,needtodelete::class.java)
                intent.putExtra("pathname",textviewobj.path)
                intent.putExtra("type",textviewobj.type)
                println(textviewobj.path)
                if (textviewobj.type == "video"){
                    val videobuffe = pathx + "videobuffe/" + textviewobj.name.substring(2,3) + ".jpg"
                    println(videobuffe)
                    intent.putExtra("withpath",videobuffe)
                }
                startActivityForResult(intent,2)
            }
            true

        }
        imageorvideolist.setOnItemClickListener { _, _, position, _ ->
            val textviewobj = imageorvideoList[position]
            val vibrator = MyApplication.context.getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(34) //单位是ms
            if(textviewobj.type == "image"){
                intent = Intent(this,showimage::class.java)
                intent.putExtra("imagepath",textviewobj.path)
                intent.putExtra("imagename",textviewobj.name)
                startActivity(intent)
            }
            if(textviewobj.type == "video"){
                intent = Intent(this,showvideo::class.java)
                intent.putExtra("imagepath",textviewobj.path)
                intent.putExtra("imagename",textviewobj.name)
                startActivity(intent)
            }
        }
        recordlist.setOnItemClickListener { _, _, position, _ ->
            val textviewobj = recordList[position]
            val vibrator = MyApplication.context.getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(34) //单位是ms
            if(textviewobj.type == "record"){
                intent = Intent(this,showaudio::class.java)
                intent.putExtra("recordpath",textviewobj.path)
                intent.putExtra("recordname",textviewobj.name)
                startActivity(intent)
            }
        }
        recordlist.setOnItemLongClickListener { _, _, position, _ ->
            val textviewobj = recordList[position]
            if(textviewobj.type == "record" && isrewrite) {
                intent = Intent(this, needtodelete::class.java)
                intent.putExtra("pathname", textviewobj.path)
                intent.putExtra("type", textviewobj.type)
                startActivityForResult(intent, 2)
            }
            true
        }



        edittmood.setOnClickListener {
            val intent = Intent(this,DailyAccounts::class.java)
            intent.putExtra("name",name)
            intent.putExtra("rewrite",true)
            intent.putExtra("path",pathx)
            startActivityForResult(intent,3)
        }
        edittemplate.setOnClickListener {
            val intent = Intent(this,select_template::class.java)
            intent.putExtra("rewrite",true)
            intent.putExtra("path",pathx)
            startActivityForResult(intent,4)
        }


        editst_background.setOnClickListener {
            if(isrewrite) {
                try{
                    DeleteFileUtil.delete(File(cacheDir.absolutePath,"shot.jpg").absolutePath)
                    FileUtils.savebitmap(rsBlur(this,viewConversionBitmap(mScrollView)!!,8),cacheDir.absolutePath,"shot.jpg",80)
                }catch (e:Exception){
                    e.printStackTrace()
                }
                val intent = Intent(this,edit_daily_text::class.java)
                intent.putExtra("text",editsometext.text.toString())
                intent.putExtra("color",textColor.toInt())
                startActivityForResult(intent,7)
            }
        }
        editsometext.setOnClickListener {
            if(isrewrite) {
                try{
                    DeleteFileUtil.delete(File(cacheDir.absolutePath,"shot.jpg").absolutePath)
                    FileUtils.savebitmap(rsBlur(this,viewConversionBitmap(mScrollView)!!,8),cacheDir.absolutePath,"shot.jpg",80)
                }catch (e:Exception){
                    e.printStackTrace()
                }
                val intent = Intent(this,edit_daily_text::class.java)
                intent.putExtra("text",editsometext.text.toString())
                intent.putExtra("color",textColor.toInt())
                startActivityForResult(intent,7)
            }
        }
        editsometext_edit.setOnClickListener {
            if(isrewrite) {
                try{
                    DeleteFileUtil.delete(File(cacheDir.absolutePath,"shot.jpg").absolutePath)
                    FileUtils.savebitmap(rsBlur(this,viewConversionBitmap(mScrollView)!!,8),cacheDir.absolutePath,"shot.jpg",80)
                }catch (e:Exception){
                    e.printStackTrace()
                }
                val intent = Intent(this,edit_daily_text::class.java)
                intent.putExtra("text",editsometext.text.toString())
                intent.putExtra("color",textColor.toInt())
                startActivityForResult(intent,7)
            }
        }
        edit_addmedia1.setOnClickListener {
            DeleteFileUtil.delete(File(cacheDir.absolutePath,"shot.jpg").absolutePath)
            FileUtils.savebitmap(rsBlur(this,viewConversionBitmap(mScrollView)!!,8),cacheDir.absolutePath,"shot.jpg",80)
            val intent = Intent(this,Select_media_access::class.java)
            intent.putExtra("path",path)
            intent.putExtra("imagetimes",imagetimes)
            intent.putExtra("videotimes",videotimes)
            intent.putExtra("recordtimes",recordtimes)
            startActivityForResult(intent,1)
        }
        edit_addmedia2.setOnClickListener {
            DeleteFileUtil.delete(File(cacheDir.absolutePath,"shot.jpg").absolutePath)
            FileUtils.savebitmap(rsBlur(this,viewConversionBitmap(mScrollView)!!,8),cacheDir.absolutePath,"shot.jpg",80)
            val intent = Intent(this,Select_media_access::class.java)
            intent.putExtra("path",path)
            intent.putExtra("imagetimes",imagetimes)
            intent.putExtra("videotimes",videotimes)
            intent.putExtra("recordtimes",recordtimes)
            startActivityForResult(intent,1)
        }
        edit_addmedia2.addClickScale()
        editsometext_edit.addClickScale()
        edittemplate.addClickScale()
        edittmood.addClickScale()
        val ac:String? = intent.getStringExtra("ac") ?: null
        if(ac == "shot"){
            thread {
                Thread.sleep(500)
                runOnUiThread {
                    shot_to_img()
                }
            }
        }
    }




    private fun getAllChildViews(view: View?): List<View> {
        val allChildViews: MutableList<View> = ArrayList()
        if (view != null && view is ViewGroup) {
            val vp = view
            for (i in 0 until vp.childCount) {
                val viewChild = vp.getChildAt(i)
                allChildViews.add(viewChild)
            }
        }
        return allChildViews
    }

    fun ifrewrite(yes:Boolean) {
        editsometext.isFocusable = false
        editsometext.keyListener=null
        if(!yes) {
            isrewrite = false
            edit_addmedia1.visibility = View.GONE
            edit_addmedia2.visibility = View.GONE
            edittmood.visibility = View.GONE
            edittemplate.visibility = View.GONE
            editsometext_edit.visibility = View.GONE
            mStickerLayout.visibility = View.VISIBLE
            show_more_ac()
            editsometext.setHint("")
            DeleteFileUtil.delete(pathx+"text.txt")
            DeleteFileUtil.delete(pathx+"textcolor.data")
            FileUtils.writeTxtToFile(editsometext.text.toString(), pathx, "text.txt")
            Log.d("debug,color",textColor.toString())
            FileUtils.writeTxtToFile(textColor.toString(), pathx, "textcolor.data")
            uploadmood()

        }else{
            isrewrite = true
            if(moodtext.length() >5) moodtext.text = moodtext.text.toString().substring(0,5)+"..."
            edit_addmedia2.visibility = View.VISIBLE
            edittmood.visibility = View.VISIBLE
            edittemplate.visibility = View.VISIBLE
            editsometext_edit.visibility = View.VISIBLE
            mStickerLayout.visibility = View.GONE
            hide_more_ac()
            MyApplication.newwrite = true
            editsometext.setHint(edithint)
            if((imagetimes-1) == 0 && (recordtimes-1) != 0){
                edit_addmedia1.visibility=View.INVISIBLE
            }else{
                edit_addmedia1.visibility=View.VISIBLE
            }
        }
    }

    fun initimageorvideolist(){

        val pathofimage = pathx + "image/"
        val pathofvideo = pathx + "video/"
        val pathofvideobuffe = pathx + "videobuffe/"
        var Filenamesofimage = GFN(pathofimage)
        var Filenamesofvideo = GFN(pathofvideo)

        imageorvideoList.clear()

        val lp2: ViewGroup.LayoutParams = imageorvideolist.getLayoutParams()
        lp2.width = 320
        lp2.height = 0
        if(Filenamesofimage.size != 0) {
            for (i in 0..Filenamesofimage.size - 1) {
                imageorvideoList.add(
                    TextviewButtonList(
                        "图片" + (i+1).toString(),
                        0,
                        pathofimage + Filenamesofimage[i],
                        "image",
                        imagepath = pathofimage + Filenamesofimage[i],
                        index = i
                    )
                )
                if(Filenamesofimage[i].substring(0,1) != (i+1).toString()) {
                    FileUtils.renamefile(
                        imageorvideoList[i].path,
                        pathofimage + (i + 1).toString() + ".jpg"
                    )
                    imageorvideoList[i].path = pathofimage + (i + 1).toString() + ".jpg"
                    imageorvideoList[i].imagepath = pathofimage + (i + 1).toString() + ".jpg"
                }
                println("addimage")
            }
        }
        if(Filenamesofvideo.size != 0){
            for(i in 0..Filenamesofvideo.size-1){
                var topsize = Filenamesofimage.size + i
                imageorvideoList.add(
                    TextviewButtonList(
                        "视频"+Filenamesofvideo[i].substring(0,1),
                        0,
                        pathofvideo + Filenamesofvideo[i],
                        "video",
                        imagepath = pathofvideobuffe+Filenamesofvideo[i].substring(0,1)+".jpg",
                        index = i
                    )
                )
                if(Filenamesofvideo[i].substring(0,1) != (i+1).toString()) {
                    FileUtils.renamefile(
                        imageorvideoList[topsize].path,
                        pathofvideo + (i + 1).toString() + ".mp4"
                    )
                    imageorvideoList[topsize].path = pathofvideo + (i + 1).toString() + ".mp4"
                }
                if(imageorvideoList[topsize].imagepath != pathofvideobuffe+(i+1).toString()+".jpg") {
                    FileUtils.renamefile(
                        imageorvideoList[topsize].imagepath,
                        pathofvideobuffe + (i + 1).toString() + ".jpg"
                    )
                    imageorvideoList[topsize].imagepath = pathofvideobuffe + (i + 1).toString() + ".jpg"
                }
                println("addvideo")
            }
        }
        lp2.height = imageorvideoList.size * 320
        println(imageorvideoList.size * 320)
        imageorvideolist.setLayoutParams(lp2);
        adapterofimageorvideo = TextviewButtonListAdapter(this,R.layout.imageorvideolist_item,imageorvideoList)
        imageorvideolist.adapter = adapterofimageorvideo

        adapterofimageorvideo.notifyDataSetChanged()

    }
    fun initrecordlist(){
        val pathofrecord = pathx + "record/"
        var Filenamesofrecord = GFN(pathofrecord)

        recordList.clear()

        val lp3: ViewGroup.LayoutParams = recordlist.getLayoutParams()
        lp3.height = 0
        if(Filenamesofrecord.size != 0){
            for(i in 0..Filenamesofrecord.size-1){
                recordList.add(TextviewButtonList("录音"+(i+1).toString(),
                    R.mipmap.isrecord,
                    pathofrecord + Filenamesofrecord[i],
                    "record")
                )
                if(Filenamesofrecord[i].substring(0,1) != (i+1).toString()){
                    FileUtils.renamefile(recordList[i].path,pathofrecord+(i+1).toString()+".wav")
                    recordList[i].path = pathofrecord+(i+1).toString()+".wav"
                }
                lp3.height += 70
            }
        }else{
            lp3.height = 0
        }
        adapterofrecord = TextviewButtonListAdapter(this,R.layout.recordlist_item,recordList)
        recordlist.adapter = adapterofrecord
        recordlist.setLayoutParams(lp3)

        adapterofrecord.notifyDataSetChanged()
        if((imagetimes-1) == 0 && (recordtimes-1) != 0){
            edit_addmedia1.visibility=View.INVISIBLE
        }else{
            edit_addmedia1.visibility=View.VISIBLE
        }
    }


    fun GFN(dirpathx:String):MutableList<String>{
        val fileNames: MutableList<String> = mutableListOf()
        //在该目录下走一圈，得到文件目录树结构
        if(File(dirpathx).isDirectory) {
            val fileTree: FileTreeWalk = File(dirpathx).walk()
            fileTree.maxDepth(1) //需遍历的目录层次为1，即无须检查子目录
                .filter { it.isFile } //只挑选文件，不处理文件夹
                .filter { it.extension in listOf("m4a", "jpg", "mp4","wav","mp3") }//选择扩展名为txt或者mp4的文件
                .forEach { fileNames.add(it.name) }//循环 处理符合条件的文件
        }
        fileNames.sort()
        return fileNames
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> if(resultCode == RESULT_OK) {
                val type = data?.getStringExtra("type").toString()
                Log.d("onActivityResult","已成功返回数据")
                Log.d("onActivityResult","type"+type.toString())
                when(type){
                    "image" -> {imagetimes += 1;initimageorvideolist()}
                    "video" -> {videotimes += 1;initimageorvideolist()}
                    "record" -> {recordtimes +=1;initrecordlist()}
                    "backrecord" -> {
                        val intent = Intent(this,sound_recording_dialog::class.java)
                        intent.putExtra("recordtimes",recordtimes)
                        intent.putExtra("path",pathx)
                        startActivityForResult(intent,1)
                    }
                    else -> println("太奇怪了，梁智超")
                }
                Log.d("onActivityResult","imagetimes"+imagetimes.toString())
                Log.d("onActivityResult","imagetimes"+videotimes.toString())
                if((imagetimes-1) == 0 && (recordtimes-1) != 0){
                    edit_addmedia1.visibility=View.INVISIBLE
                }else{
                    edit_addmedia1.visibility=View.VISIBLE
                }
            }
            2 -> if(resultCode == RESULT_OK){
                val type = data?.getStringExtra("type").toString() ?: null
                val del = data?.getBooleanExtra("delete",false) as Boolean
                if(del == true){
                    when (type) {
                        "image" -> if(imagetimes!=0){
                            imagetimes -= 1
                        }
                        "video" -> if(videotimes!=0){
                            videotimes -= 1
                        }
                        "record" ->if(recordtimes!=0){
                            recordtimes -= 1
                        }
                    }
                }
                Log.d("onActivityResult","del!"+type)
                if((type == "image" || type == "video") && del){
                    initimageorvideolist()
                }else if(type == "record" && del){
                    initrecordlist()
                }
                if((imagetimes-1) == 0 && (recordtimes-1) != 0){
                    edit_addmedia1.visibility=View.INVISIBLE
                }else{
                    edit_addmedia1.visibility=View.VISIBLE
                }
            }

            3 -> if(resultCode == RESULT_OK) {
                uploadmood()
                if(isrewrite)
                    if(moodtext.length() >5)
                        moodtext.text = moodtext.text.toString().substring(0,5)+"..."
            }

            4 -> if(resultCode == RESULT_OK) {
                uploadbackground()
            }
            5 -> if(resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition()
                }else{
                    finish()
                }
            }
            7 -> if(resultCode == RESULT_OK) {
                try{
                    if(data != null) {
                        editsometext.setText(data.getStringExtra("newtext").toString())
                        editsometext.setTextColor(resources.getColor(MyApplication.nuberToTextColor[data.getIntExtra("newcolor",1)]!!))
                        textColor = data.getIntExtra("newcolor",1)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imageorvideoList.clear()
        recordList.clear()
        adapterofimageorvideo.clear()
        adapterofrecord.clear()
        isonDestroy = true
        CleanRAMhelper().removeSelfFromParent(background)
        System.gc()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        //处理回调
        xtcCallback.handleIntent(intent, this)
    }

    fun uploadmood(){
        try{
            val moodText:String = FileUtils.readTxtFile(pathx+"mood.txt").toString()
            val moodTextsplit = moodText.split("$[%|!|%]$")
            val moodspath = "/data/data/com.haoduyoudu.DailyAccounts/assest/mood/"
            if(moodTextsplit.size == 2){
                Glide.with(this)
                    .load(moodspath+moodTextsplit[0]+".png")
                    .into(moodimage)
                moodtext.setText(moodTextsplit[1])
            }else{
                Glide.with(this)
                    .load(MyApplication.NumberToMoodImage[moodText.toInt()])
                    .into(moodimage)
                moodtext.setText(MyApplication.NumberToMoodSay[moodText.toInt()])
            }
        }catch (e:Exception){
            e.printStackTrace()
            Glide.with(this)
                .load(R.mipmap.isnoneface)
                .into(moodimage)
            moodtext.setText("心情失踪啦！")
        }
    }

    fun uploadbackground(){
        var textdata:String? = null
        try {
            textdata = FileUtils.readTxtFile(pathx+"template.data")
            background.setBackgroundResource(MyApplication.NuberToTemplate[textdata.toInt()]!!.toInt())
        }catch (e:Exception){
            if (textdata != "")
                e.printStackTrace()
            background.setBackgroundResource(R.mipmap.moren)
        }
    }

    fun shot_to_img(){
        try {
            close_ac.visibility = View.INVISIBLE
            thread {
                Thread.sleep(300)
                runOnUiThread {
                    try{
                        val share_image = viewConversionBitmap(f_background)!!
                        close_ac.visibility = View.VISIBLE
                        FileUtils.savebitmap(share_image,"/sdcard/DCIM/Camera/",System.currentTimeMillis().toString()+".png",100,Bitmap.CompressFormat.PNG)

                        Toast.makeText(this,"已保存到相册",Toast.LENGTH_SHORT).show()
                        //第一步：创建XTCImageObject 对象，并设置bitmap属性为要分享的图片
                        val xtcImageObject = XTCImageObject();
                        xtcImageObject.setBitmap(share_image);
                        //如果图片在公共目录，可以直接设置图片路径即可

                        //第二步：创建XTCShareMessage对象，并将shareObject属性设置为xtcTextObject对象
                        val xtcShareMessage = XTCShareMessage();
                        xtcShareMessage.setShareObject(xtcImageObject);

                        //第三步：创建SendMessageToXTC.Request对象，并设置message属性为xtcShareMessage
                        val request = SendMessageToXTC.Request();
                        request.setMessage(xtcShareMessage);
                        request.setFlag(1)

                        //第四步：创建ShareMessageManagr对象，调用sendRequestToXTC方法，传入SendMessageToXTC.Request对象和AppKey
                        ShareMessageManager(this).sendRequestToXTC(request, "a81252c4145a48a9a52f0d3015a891d9");
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this,"分享失败",Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun load_sks(){
        if (File(pathx+"sitcker.sk").exists()){
            val fileIn = FileInputStream(pathx+"sitcker.sk")
            val fin = ObjectInputStream(fileIn)
            val allposdata = (fin.readObject() as ArrayList<FloatArray>)
            val allbitmapdata = (fin.readObject() as ArrayList<ByteArray>)
            fin.close()
            fileIn.close()
            mStickerLayout.removeAllSticker()
            for (i in 0..(allposdata.size-1)){
                val bitmap = BitmapFactory.decodeByteArray(allbitmapdata[i], 0, allbitmapdata[i].size)
                val sticker = Sticker(bitmap)
                sticker.matrix.setValues(allposdata[i])
                mStickerLayout.addSticker(sticker)
            }
            mStickerLayout.updata()
        }
    }

    fun show_more_ac(){
        if(!more_ac_show){
            more_ac_show = true
        }
    }

    fun hide_more_ac(){
        if(more_ac_show){
            more_ac_show = false
        }
    }
    /**
     * @param linearLayout 要转化为图片的布局
     */
    fun viewConversionBitmap(v: View,config:Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
        val w = v.width
        val h = v.height
        val bmp = Bitmap.createBitmap(w, h, config)
        val c = Canvas(bmp)
        c.setDrawFilter(PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG and Paint.FILTER_BITMAP_FLAG))
        /** 如果不设置canvas画布为白色，则生成透明  */
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp
    }
    fun uptextcolor(){
        try {
            val textdata:String = FileUtils.readTxtFile(pathx+"textcolor.data")
            editsometext.setTextColor(resources.getColor(MyApplication.nuberToTextColor[textdata.toInt()]!!))
            textColor = textdata.toInt()
        }catch (e:Exception){
            textColor = 1
            e.printStackTrace()
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
    override fun onResp(isSuccess: Boolean, response: BaseResponse?) {
        if(isSuccess){
            Toast.makeText(this,"分享成功",Toast.LENGTH_SHORT).show()
        }else{
            if(response?.getCode()!=2)
                Toast.makeText(this,"分享失败,错误码${response?.getCode() ?: "None"}",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onReq(request: ShowMessageFromXTC.Request?) {
        //to-do
    }

}
