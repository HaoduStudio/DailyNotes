package com.haoduyoudu.DailyAccounts

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xtc.shareapi.share.interfaces.IResponseCallback
import kotlinx.android.synthetic.main.activity_aboutsoftware.*
import com.xtc.shareapi.share.manager.ShareMessageManager

import com.xtc.shareapi.share.communication.SendMessageToXTC

import android.graphics.BitmapFactory
import android.view.View
import com.xtc.shareapi.share.communication.BaseResponse
import com.xtc.shareapi.share.communication.ShowMessageFromXTC
import com.xtc.shareapi.share.manager.XTCCallbackImpl

import com.xtc.shareapi.share.shareobject.XTCShareMessage

import com.xtc.shareapi.share.shareobject.XTCAppExtendObject
import kotlin.Exception
import kotlin.concurrent.thread
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.SHIELD_SHARE_ACTON
import java.io.*
import android.graphics.Bitmap
import android.graphics.Canvas
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.COUNT_OF_DAYS_OF_COLOREGG_TO_APPEAR
import com.xtc.shareapi.share.sharescene.Chat

class aboutsoftware : AppCompatActivity(){

    var cantouch = true
    val touchdata = ArrayList<Long>()
    val pathofdailyaccounts = "/sdcard/Android/data/com.haoduyoudu.DailyAccounts/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutsoftware)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || SHIELD_SHARE_ACTON){
            sharefriendf.visibility=View.GONE
        }

        versions.setOnClickListener {
            touchdata.add(System.currentTimeMillis())
            if(touchdata.size!=0){
                thread {
                    try{
                        val beforenum = touchdata.size
                        Thread.sleep(500)
                        if(beforenum == touchdata.size)
                            touchdata.clear()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                if(touchdata.size>=7){
                    try{
                        if(GFN(pathofdailyaccounts).size >= COUNT_OF_DAYS_OF_COLOREGG_TO_APPEAR){
                            if(!File("/data/data/com.haoduyoudu.DailyAccounts/assest/moresticker/").exists()){
                                Toast.makeText(this,getString(R.string.about_mubiaodacheng),Toast.LENGTH_SHORT).show()
                                val rpath = "/data/data/com.haoduyoudu.DailyAccounts/"
                                FileUtils.makeRootDirectory(rpath + "assest/" + "moresticker/")
                                for (i in assets.list("moresticker")!!) {
                                    copyAssets(MyApplication.context, i.toString(),"/data/data/com.haoduyoudu.DailyAccounts/assest/moresticker/","assets/moresticker/")
                                }
                                val newintent = Intent(this,caidan::class.java)
                                newintent.putExtra("type","award")
                                startActivity(newintent)
                            }
                        }else{
                            Toast.makeText(this,getString(R.string.about_caidan_tips, COUNT_OF_DAYS_OF_COLOREGG_TO_APPEAR),Toast.LENGTH_SHORT).show()
                        }
                        touchdata.clear()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            } }

            zhixiemeigong.setOnLongClickListener {
                val newintent = Intent(this,caidan::class.java)
                newintent.putExtra("type","image")
                startActivity(newintent)
                true
            }
        if(MyApplication.firstLoadaboutBg){
            tempview.visibility=View.VISIBLE
            cantouch=false
            thread {
                runOnUiThread { fadein(startview) }
                Thread.sleep(2500)
                runOnUiThread { fadeout(startview, 1000) }
                Thread.sleep(1000)
                runOnUiThread { tempview.visibility = View.GONE }
                cantouch = true
            }
            MyApplication.firstLoadaboutBg = false
        }
        share_to_friend.addClickScale(0.8f)
        im1.setScale(0.0f)
        val lp: ViewGroup.LayoutParams = im1.getLayoutParams()
        lp.height = 0
        im1.setLayoutParams(lp)
        var istouch = false
        Tv1.setOnLongClickListener {
            try {
                if (!istouch) {
                    Toast.makeText(this,getString(R.string.about_caidan_tips2),Toast.LENGTH_LONG).show()
                    im1.addScale(0.0f, 1.0f, 500)
                    lp.height = 320
                    im1.setLayoutParams(lp)
                    istouch = true
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
            true
        }
        im1.setOnClickListener {
            try {
                lp.height = 0
                im1.setLayoutParams(lp)
            }finally {
                istouch = false
            }
        }
        share_to_friend.setOnClickListener {

            try {
                val xtcAppExtendObject = XTCAppExtendObject()


                xtcAppExtendObject.startActivity = MainActivity::class.java.name

                xtcAppExtendObject.extInfo = ""

                val xtcShareMessage = XTCShareMessage()
                xtcShareMessage.shareObject = xtcAppExtendObject
                xtcShareMessage.setThumbImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))

                xtcShareMessage.description = getString(R.string.share_software_text)

                val request = SendMessageToXTC.Request()
                request.message = xtcShareMessage
                request.setFlag(1)
                val shareMessageManager = ShareMessageManager(this)
                shareMessageManager.sendRequestToXTC(request, "a81252c4145a48a9a52f0d3015a891d9")
                Toast.makeText(this,getString(R.string.share_software_tips),Toast.LENGTH_SHORT).show()

            }catch (e:Exception){
                Toast.makeText(this,getString(R.string.share_fail),Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }


    private fun fadein(v: View,duration:Long = 1500L){
        v.setAlpha(0f);
        v.setVisibility(View.VISIBLE);
        v.animate()
            .alpha(1f)
            .setDuration(duration)
            .setListener(null);
    }
    private fun fadeout(v:View,duration:Long = 1500L){
        v.animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    v.setVisibility(View.GONE)
                }
            })
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (cantouch) {
            super.dispatchTouchEvent(ev) //OK touch

        } else {
            false
        }

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
    fun copyAssets(context: Context, FileName:String, dir:String, assestpath:String):String{


        val dir = File(dir);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }

        val file = File(dir, FileName);
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

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
            } catch (e: IOException) {
                e.printStackTrace();
            } finally {
                outputStream?.flush();
                outputStream?.close();
                inputStream?.close();
            }
        }
        return file.getPath();
    }
    private fun getBitmap(context: Context, vectorDrawableId: Int): Bitmap? {
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val vectorDrawable = context.getDrawable(vectorDrawableId)
            bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            vectorDrawable.draw(canvas)
        } else {
            bitmap = BitmapFactory.decodeResource(context.resources, vectorDrawableId)
        }
        return bitmap
    }

}
