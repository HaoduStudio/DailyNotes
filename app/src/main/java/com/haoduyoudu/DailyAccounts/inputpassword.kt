package com.haoduyoudu.DailyAccounts

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.bumptech.glide.Glide
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.CLOSE_PASSWORD
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.SET_PASSWORD
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.RESET_PASSWORD
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.INPUT_PASSWORD
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.MAX_PASSWORD_ERROR_TIMES
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.DIGIT_OF_PASSWORD
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.LOCK_TIME
import kotlinx.android.synthetic.main.activity_inputpassword.*
import java.io.File
import java.lang.StringBuilder
import kotlin.concurrent.thread

class inputpassword : AppCompatActivity(), View.OnClickListener{
    val IS_DISPLAY_TIPS = 1
    val IS_INPUT_PASSWORD = 2
    val IS_LOCKING_UI = 3

    var errortimes = 0
    lateinit var numbuttons:ArrayList<View>
    lateinit var actionbuttons:ArrayList<View>
    lateinit var dissplayview:ArrayList<ImageView>
    lateinit var countDownTimer: CountDownTimer
    var originalpassword:ArrayList<Int>? = ArrayList<Int>()
    var originalpassword_input:ArrayList<Int>? = ArrayList<Int>()
    var beforepassword:ArrayList<Int>? = ArrayList<Int>()
    var nowpasword = ArrayList<Int>()
    val rootdata = "/data/data/com.haoduyoudu.DailyAccounts/"
    var type = SET_PASSWORD   //default value
    var nowstatus = 0
    var isover = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inputpassword)
        numbuttons = arrayListOf(passnum0,passnum1,passnum2,passnum3,passnum4,passnum5,passnum6,passnum7,passnum8,passnum9)
        actionbuttons = arrayListOf(passnumback,passnumdel)
        dissplayview = arrayListOf(pass1,pass2,pass3)
        for(i in (numbuttons+actionbuttons)) i.setOnClickListener(this)
        type = intent.getIntExtra("type",1)
        oncleanpassword()

        when(type){
            RESET_PASSWORD -> {
                originalpassword = getbeforepassword()
                if(originalpassword == null || originalpassword?.size!= DIGIT_OF_PASSWORD){
                    Toast.makeText(this,"系统错误",Toast.LENGTH_SHORT).show()
                    val file = File(rootdata+"password.data")
                    DeleteFileUtil.delete(file.absolutePath)
                    val intent = Intent()
                    setResult(RESULT_CANCELED,intent)
                    finish()
                }else{
                    showtips("输入原密码")
                }
            }
            INPUT_PASSWORD,CLOSE_PASSWORD -> {
                originalpassword = getbeforepassword()
                if(originalpassword == null || originalpassword?.size != DIGIT_OF_PASSWORD){
                    Toast.makeText(this,"系统错误",Toast.LENGTH_SHORT).show()
                    val file = File(rootdata+"password.data")
                    DeleteFileUtil.delete(file.absolutePath)
                    android.os.Process.killProcess(android.os.Process.myPid())
                }else{
                    if(type != CLOSE_PASSWORD){
                        if(File(rootdata,"FORGETPASS.dt").exists()){
                            thread {
                                val endtime = FileUtils.readTxtFile(File(rootdata,"FORGETPASS.dt").absolutePath).toLong()
                                runOnUiThread {
                                    showtips("输入密码")
                                }
                                Thread.sleep(1000)
                                while (!isover){
                                    val howend = endtime-System.currentTimeMillis()
                                    if(howend <= 0){
                                        DeleteFileUtil.delete(File(rootdata,"LOCKED.dt").absolutePath)
                                        DeleteFileUtil.delete(File(rootdata,"FORGETPASS.dt").absolutePath)
                                        DeleteFileUtil.delete(File(rootdata,"password.data").absolutePath)
                                        runOnUiThread {
                                            Toast.makeText(this,"你设置的\"忘记密码\"功能时间已到，已关闭密码",Toast.LENGTH_SHORT).show()
                                        }
                                        val intent = Intent()
                                        setResult(RESULT_OK,intent)
                                        finish()
                                        break
                                    }
                                    Thread.sleep(1000)
                                    if(nowstatus != IS_INPUT_PASSWORD && nowstatus != IS_LOCKING_UI){
                                        runOnUiThread {
                                            if(howend / (1000*60*60) != 0L){
                                                "${(howend/(1000*60*60)).toInt()}小时后自动解锁".also { text_tips.text=it }
                                            }else if(howend/(1000*60) != 0L){
                                                "${(howend/(1000*60)).toInt()}分钟后自动解锁".also { text_tips.text=it }
                                            }else if(howend/(1000) != 0L){
                                                "还有${(howend/(1000)).toInt()}秒后自动解锁".also { text_tips.text=it }
                                            }else{
                                                "已解锁".also { text_tips.text=it }
                                            }
                                        }
                                    }
                                }
                            }
                        }else{
                            showtips("输入密码")
                        }
                    }else{
                        showtips("输入密码")
                    }
                }
            }
        }

        val lockfile = File(rootdata+"LOCKED.dt")
        if(lockfile.exists())
            onlockpassword(true)

    }

    override fun onStart() {
        super.onStart()
        for(i in numbuttons)
            i.addScale(0.1f,1f,300)
    }
    override fun onClick(v: View?) {
        if(v!=null && (nowstatus != IS_LOCKING_UI || v==passnumback))
            if(numbuttons.indexOf(v) != -1){
                val clicknum = numbuttons.indexOf(v)
                Log.d("tap","num $clicknum")
                if (nowstatus != IS_INPUT_PASSWORD) showpassword()
                when(type){
                    INPUT_PASSWORD, CLOSE_PASSWORD -> {
                        forwardapassword(clicknum,nowpasword)
                        if(nowpasword.size == DIGIT_OF_PASSWORD){
                            if(originalpassword!!.equals(nowpasword)){
                                if(type == CLOSE_PASSWORD){
                                    DeleteFileUtil.delete(File(rootdata,"password.data").absolutePath)
                                    Toast.makeText(this,"已关闭密码",Toast.LENGTH_SHORT).show()
                                }
                                DeleteFileUtil.delete(File(rootdata,"FORGETPASS.dt").absolutePath)
                                val intent = Intent()
                                setResult(RESULT_OK,intent)
                                finish()
                            }else{
                                nowpasword.clear()
                                onpassworderror()
                                if(type == INPUT_PASSWORD){
                                    if(errortimes >= MAX_PASSWORD_ERROR_TIMES){
                                        onlockpassword()
                                    }else{
                                        Toast.makeText(this,"密码错误\n还可尝试${MAX_PASSWORD_ERROR_TIMES-errortimes}次",Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    SET_PASSWORD -> {
                        if(beforepassword!!.size < DIGIT_OF_PASSWORD){
                            forwardapassword(clicknum,beforepassword!!)
                            if(beforepassword!!.size == DIGIT_OF_PASSWORD){
                                showtips("再次输入密码")
                                oncleanpassword()
                            }
                        }else if(nowpasword.size < DIGIT_OF_PASSWORD){
                            forwardapassword(clicknum,nowpasword)
                            if(nowpasword.size == DIGIT_OF_PASSWORD){
                                if(beforepassword!!.equals(nowpasword)){
                                    createpassword(nowpasword)
                                    val intent = Intent()
                                    setResult(RESULT_OK,intent)
                                    Toast.makeText(this,"设置成功",Toast.LENGTH_SHORT).show()
                                    finish()
                                }else{
                                    onpassworderror(false)
                                    Toast.makeText(this,"两次密码不相同",Toast.LENGTH_SHORT).show()
                                    beforepassword!!.clear()
                                    nowpasword.clear()
                                }
                            }
                        }
                    }
                    RESET_PASSWORD -> {
                        if (originalpassword_input!!.size == DIGIT_OF_PASSWORD){
                            if (beforepassword!!.size < DIGIT_OF_PASSWORD) {

                                forwardapassword(clicknum, beforepassword!!)
                                if (beforepassword!!.size == DIGIT_OF_PASSWORD) {
                                    showtips("再次输入新密码")
                                    oncleanpassword()
                                }
                            } else if (nowpasword.size < DIGIT_OF_PASSWORD) {
                                forwardapassword(clicknum, nowpasword)
                                if (nowpasword.size == DIGIT_OF_PASSWORD) {
                                    if(beforepassword!!.equals(nowpasword)){
                                        val intent = Intent()
                                        setResult(RESULT_OK, intent)
                                        createpassword(nowpasword)
                                        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }else {
                                        onpassworderror(false)
                                        Toast.makeText(this, "两次密码不相同", Toast.LENGTH_SHORT).show()
                                        beforepassword!!.clear()
                                        nowpasword.clear()
                                    }
                                }
                            }
                        }else{
                            forwardapassword(clicknum,originalpassword_input!!)
                            if(originalpassword_input!!.size == DIGIT_OF_PASSWORD){
                                if(!originalpassword!!.equals(originalpassword_input)){
                                    originalpassword_input!!.clear()
                                    onpassworderror(false)
                                    Toast.makeText(this, "原密码不正确", Toast.LENGTH_SHORT).show()
                                }else{
                                    showtips("输入新密码")
                                    oncleanpassword()
                                }
                            }
                        }
                    }
                }
            }else if(actionbuttons.indexOf(v) != -1){
                when(v){
                    passnumback -> {
                        when(type){
                            SET_PASSWORD,RESET_PASSWORD,CLOSE_PASSWORD -> {  //safe
                                val intent = Intent()
                                setResult(RESULT_CANCELED,intent)
                                finish()
                            }
                            INPUT_PASSWORD -> {   //not safe
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }
                        }
                    }
                    passnumdel -> {
                        when(type){
                            SET_PASSWORD -> {
                                if(beforepassword!!.size < DIGIT_OF_PASSWORD){
                                    backapsssword(beforepassword!!)
                                }else if(nowpasword.size < DIGIT_OF_PASSWORD){
                                    backapsssword(nowpasword)
                                }
                            }
                            RESET_PASSWORD -> {
                                if(originalpassword_input!!.size == DIGIT_OF_PASSWORD) {
                                    if (beforepassword!!.size < DIGIT_OF_PASSWORD) {
                                        backapsssword(beforepassword!!)
                                    } else if (nowpasword.size < DIGIT_OF_PASSWORD) {
                                        backapsssword(nowpasword)
                                    }
                                }else {
                                    backapsssword(originalpassword_input!!)
                                }
                            }
                            INPUT_PASSWORD, CLOSE_PASSWORD -> {
                                backapsssword(nowpasword)
                            }
                        }
                    }
                }
            }
    }
    fun oncleanpassword(){
        for(i in dissplayview)
            Glide.with(this).load(R.mipmap.none_pass).into(i)
    }
    fun onpassworderror(addtimes:Boolean = true){
        oncleanpassword()
        if(addtimes) errortimes+=1
        ObjectAnimator.ofFloat(password, "translationX",  0f, -100f, 0f, 100f, 0f).apply {
            duration = 600
            repeatCount = 0
            interpolator= LinearOutSlowInInterpolator()
            repeatMode = ValueAnimator.RESTART
        }.start()
    }
    fun onlockpassword(fromlast:Boolean = false){
        errortimes = 0
        password.visibility = View.GONE
        text_tips.visibility = View.VISIBLE
        val file = File(rootdata+"LOCKED.dt")
        var cloktime = 0L
        if(!File(rootdata,"FORGETPASS.dt").exists() && !fromlast){
            startActivity(Intent(this,forget_password::class.java))
        }
        if(fromlast){
            try{
                val time = FileUtils.readTxtFile(file.absolutePath).toLong()
                if(time>=System.currentTimeMillis()){
                    cloktime = time-System.currentTimeMillis()
                }else{
                    DeleteFileUtil.delete(file.absolutePath)
                }
            }catch (e:Exception){
                DeleteFileUtil.delete(file.absolutePath)
            }
        }else{
            FileUtils.writeTxtToFile((System.currentTimeMillis()+ LOCK_TIME).toString(),rootdata,"LOCKED.dt")
            cloktime = LOCK_TIME
        }
        if(cloktime!=0L) {
            nowstatus = IS_LOCKING_UI
            countDownTimer = object:
                CountDownTimer(cloktime, 1*1000) {
                override fun onTick(millisUntilFinished:Long) {
                    nowstatus = IS_LOCKING_UI
                    "${(millisUntilFinished/1000).toInt()}秒后再试".also { text_tips.text = it }
                }
                override fun onFinish() {
                    showpassword()
                }
            }
            countDownTimer.start()
        }
    }
    fun getbeforepassword():ArrayList<Int>?{
        val file = File(rootdata+"password.data")
        if(!file.exists())
            return null
        val data = FileUtils.readTxtFile(file.absolutePath)
        if(data == ""){
            return null
        }else{
            val resultdata = data.split(" ").filter { it!="" }.map { it.toInt() }
            for(i in resultdata) Log.d("password","last password is $i and..")
            return ArrayList<Int>().apply { addAll(resultdata) }
        }
    }
    fun createpassword(marray:ArrayList<Int>){
        try {
            val file = File(rootdata+"password.data")
            DeleteFileUtil.delete(file.absolutePath)
            val sb = StringBuilder()
            for (i in marray) sb.append(i.toString()+" ")
            if(!FileUtils.writeTxtToFile(sb.toString(),rootdata,"password.data")){
                Toast.makeText(this,"创建失败",Toast.LENGTH_SHORT).show()
                finish()
            }
        }catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(this,"创建失败",Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    fun showtips(tips:String){
        password.visibility = View.GONE
        text_tips.visibility = View.VISIBLE
        text_tips.text = tips
        nowstatus = IS_DISPLAY_TIPS
    }
    fun showpassword(){
        password.visibility = View.VISIBLE
        text_tips.visibility = View.GONE
        if(nowpasword.isNotEmpty())
            for(i in 0..nowpasword.size-1){
                Glide.with(this).load(R.mipmap.white_pass).into(dissplayview[i])
            }
        for(i in dissplayview) i.addScale(0f,1f,400)
        nowstatus = IS_INPUT_PASSWORD
    }
    fun backapsssword(marray: ArrayList<Int>){
        if(marray.isNotEmpty()) {
            try{
                marray.removeAt(marray.size - 1)
                Glide.with(this).load(R.mipmap.none_pass).into(dissplayview[marray.size])
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    fun forwardapassword(num:Int,marray:ArrayList<Int>){
        marray.add(num)
        try{
            Glide.with(this).load(R.mipmap.white_pass).into(dissplayview[marray.size-1])
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::countDownTimer.isInitialized)
            countDownTimer.cancel()
        isover = true
    }
}