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
import android.util.Log
import android.view.MotionEvent
import java.io.*


class aboutsoftware : AppCompatActivity(), IResponseCallback{

    lateinit var xtcCallback: XTCCallbackImpl
    var cantouch = true
    val touchdata = ArrayList<Long>()
    val pathofdailyaccounts = "/sdcard/Android/data/com.haoduyoudu.DailyAccounts/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutsoftware)

        xtcCallback = XTCCallbackImpl()
        xtcCallback.handleIntent(intent, this)

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
                        if(GFN(pathofdailyaccounts).size >= 21){
                            if(!File("/data/data/com.haoduyoudu.DailyAccounts/assest/moresticker/").exists()){
                                Toast.makeText(this,"恭喜达成目标！",Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this,"彩蛋：写完21篇手帐再来找我,我们不见不散!",Toast.LENGTH_SHORT).show()
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
        if(MyApplication.needupdata!=null)
            if(MyApplication.needupdata!!){
                tempview.visibility=View.VISIBLE
                cantouch=false
                thread {
                    runOnUiThread { fadein(startview) }
                    Thread.sleep(3500)
                    runOnUiThread { fadeout(startview,2300) }
                    Thread.sleep(2300)
                    runOnUiThread { tempview.visibility=View.GONE }
                    cantouch=true
                }
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
                    Toast.makeText(this,"Tips：您使用的彩蛋已过时,为什么不去版本号中多点几下呢AWA",Toast.LENGTH_LONG).show()
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

                xtcShareMessage.description = "好用的宝贝，分享给你～"

                val request = SendMessageToXTC.Request()
                request.message = xtcShareMessage
                request.setFlag(1)
                ShareMessageManager(this).sendRequestToXTC(request, "a81252c4145a48a9a52f0d3015a891d9")
                Toast.makeText(this,"《每日手帐》感谢您的支持与鼓励！",Toast.LENGTH_SHORT).show()
            }catch (e:Exception){
                Toast.makeText(this,"分享失败",Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        //处理回调
        xtcCallback.handleIntent(intent, this)
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

}
