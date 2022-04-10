package com.haoduyoudu.DailyAccounts

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_prewiew_dailyaccount.*
import kotlin.concurrent.thread
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import java.io.*


class prewiew_dailyaccount : AppCompatActivity() {
    lateinit var path:String
    var iseditingsk = false
    var Activitycantouch = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prewiew_dailyaccount)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        path = intent.getStringExtra("path").toString()
        if(!File(cacheDir,"rewrite.data").exists()){
            thread {
                Thread.sleep(500)
                runOnUiThread {
                    onpopwindowsshow()
                }
            }
        }else{
            try {
                ifeditingsk(true)
                val fileIn = FileInputStream(File(cacheDir,"rewrite.data").absolutePath)
                val fin = ObjectInputStream(fileIn)
                val scrollypos = fin.readInt()
                fin.close()
                fileIn.close()
                mScrollView.post { mScrollView.scrollTo(0,scrollypos) }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
        backtoedit.addClickScale()
        backtoedit.setOnClickListener {
            mDrawerLayout.closeDrawer(GravityCompat.END)
            Log.d("backtoedit","info of back")
            if(iseditingsk){
                val fileOut = FileOutputStream(File(cacheDir,"rewrite.data").absolutePath)
                val out = ObjectOutputStream(fileOut)
                out.writeInt(mScrollView.scrollY)
                out.close()
                fileOut.close()
            }
            ifeditingsk(false)
            setResult(RESULT_CANCELED,Intent())
            finish()
        }
        showright.setOnClickListener {
            onpopwindowsshow()
            showright.visibility=View.GONE

        }
        editsk.setOnClickListener {
            mDrawerLayout.closeDrawer(GravityCompat.END)
            if(!iseditingsk){
                ifeditingsk(true)
                if(mStickerLayout.returnAllSticker().size == 0)
                    startActivityForResult(Intent(this,CelSticker::class.java),1)
                val topsk = mStickerLayout.topSticker
                if(topsk != null)
                    mStickerLayout.focusSticker = topsk
            }else{
                startActivityForResult(Intent(this,CelSticker::class.java),1)
            }
        }
        ok.setOnClickListener {
            mDrawerLayout.closeDrawer(GravityCompat.END)
            ifeditingsk(false)
            setResult(RESULT_OK,Intent())
            finish()
        }
        mStickerLayout.removeAllSticker()
        load_sks()
        mDrawerLayout.setDrawerListener(object : DrawerListener {
            override fun onDrawerStateChanged(arg0: Int) {
                Log.e("mDrawerLayout", "statechange")
            }

            override fun onDrawerSlide(arg0: View, arg1: Float) {
                Log.e("mDrawerLayout", "slide$arg1")
            }

            override fun onDrawerOpened(arg0: View) {
                Log.e("mDrawerLayout", "open")
                val vibrator = MyApplication.context.getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(34)
            }

            override fun onDrawerClosed(arg0: View) {
                Log.e("mDrawerLayout", "colse")
                showright.visibility=View.VISIBLE
            }
        })
        if (File(cacheDir.absolutePath,"shot.png").exists()){
            val imgpath = File(cacheDir,"shot.png").absolutePath
            val lp = keepscrimg.layoutParams
            val options = BitmapFactory.Options()
            BitmapFactory.decodeFile(imgpath,options)
            lp.height=options.outHeight
            keepscrimg.layoutParams = lp

            Glide.with(this).load(imgpath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(keepscrimg)
        }else{
            Toast.makeText(this,"彩蛋x1",Toast.LENGTH_SHORT).show()
        }
        delsk.addClickScale()
        delsk.setOnClickListener {
            try{
                Log.d("test",mStickerLayout.returnAllSticker().size.toString())
                if(mStickerLayout.returnAllSticker().size == 1){
                    onClosePopEdit()
                    mStickerLayout.removeSticker(mStickerLayout.focusSticker)
                    Log.d("test",mStickerLayout.returnAllSticker().size.toString())
                    ifeditingsk(false)
                    save_sks()
                }else {
                    onClosePopEdit()
                    mStickerLayout.removeSticker(mStickerLayout.focusSticker)
                }
                val pmu = PlaymediafromresUtils(R.raw.del,this)
                pmu.play()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        mStickerLayout.setOnPopEditListener {
            onPopEdit()
        }
        mStickerLayout.setOnPopcloseEditListener {
            onClosePopEdit()
        }
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
        sk_angle_left.setOnClickListener {
            try{
                val focusSk:Sticker = mStickerLayout.getFocusSticker()
                mStickerLayout.rotateSticker(focusSk,-10f)
            }catch (e:Exception){}
        }
        sk_angle_right.setOnClickListener {
            try{
                val focusSk:Sticker = mStickerLayout.getFocusSticker()
                mStickerLayout.rotateSticker(focusSk,10f)
            }catch (e:Exception){}
        }
        sk_size.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    try {
                        val focusSk:Sticker = mStickerLayout.getFocusSticker()

                        val imageTemp = focusSk.bitmap.width
                        val values = FloatArray(9)
                        focusSk.matrix.getValues(values)
                        val nowwidth = imageTemp*values[0]
                        val scaleValue = (progress.toFloat()+50)/nowwidth
                        mStickerLayout.scaleSticker(focusSk,scaleValue,scaleValue)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
    fun ifeditingsk(yes: Boolean){
        Log.i("info","ifeditsk$yes")
        mDrawerLayout.closeDrawer(GravityCompat.END)
        if(yes){
            load_sks()
            iseditingsk = true
            mStickerLayout.setCanEdit(true)
            mScrollView.isgetfocus = false
        }else{
            save_sks()
            iseditingsk = false
            mStickerLayout.cleanAllFocus()
            mStickerLayout.setCanEdit(false)
            mScrollView.isgetfocus = true
            sk_popview.visibility = View.GONE
        }
    }
    fun onPopEdit(){
        Log.d("testpop","open")
        sk_popview.visibility = View.VISIBLE
        thread {
            Thread.sleep(50)
            try {
                val focusSk:Sticker = mStickerLayout.focusSticker
                val imageTemp = focusSk.bitmap
                val values = FloatArray(9)
                focusSk.matrix.getValues(values)
                val nowwidth = imageTemp.width*values[0]

                runOnUiThread {
                    try {
                        sk_size.progress = nowwidth.toInt()-50
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    fun onClosePopEdit(){
        Log.d("testpop","close")
        sk_popview.visibility = View.GONE
    }
    fun load_sks(){
        Log.i("info","loadsk")
        try {
            if (File(path+"sitcker.sk").exists()){
                val fileIn = FileInputStream(path+"sitcker.sk")
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
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun save_sks(){
        Log.i("info","savesk")
        try {
            val allSitcker = mStickerLayout.returnAllSticker()
            val allposdata = ArrayList<FloatArray>()
            val allbitmapdata = ArrayList<ByteArray>()
            if(allSitcker.size!=0){
                for(i in allSitcker){
                    var data = FloatArray(9)
                    i.matrix.getValues(data)
                    allposdata!!.add(data)

                    val mbitmap = i.bitmap
                    val baos = ByteArrayOutputStream()
                    mbitmap.compress(Bitmap.CompressFormat.PNG, 0, baos) //压缩位图
                    allbitmapdata.add(baos.toByteArray())
                }
                val fileOut = FileOutputStream(path+"sitcker.sk")
                val out = ObjectOutputStream(fileOut)
                out.writeObject(allposdata)
                out.writeObject(allbitmapdata)
                out.close()
                fileOut.close()
            }else{
                DeleteFileUtil.delete(path+"sitcker.sk")
                mStickerLayout.removeAllSticker()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (Activitycantouch) {
            super.dispatchTouchEvent(ev)  //可touch
        } else {
            false  //非touch
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
    private fun viewConversionBitmap(v: View,config:Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
        val w = v.width
        val h = v.height
        val bmp = Bitmap.createBitmap(w, h, config)
        val c = Canvas(bmp)
        /** 如果不设置canvas画布为白色，则生成透明  */
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp
    }
    fun onpopwindowsshow(){
        try{
            val bitmap: Bitmap = rsBlur(this,viewConversionBitmap(mDrawerLayout)!!,8)
            val bitmap1 = Bitmap.createBitmap(bitmap, 320-DisplayUtil.dip2px(55f), 0, DisplayUtil.dip2px(55f), 360)
            popbk.setImageBitmap(BitmapFillet.fillet(bitmap1,DisplayUtil.dip2px(15f),BitmapFillet.CORNER_LEFT))
            bitmap.recycle()
            bitmap1.recycle()

            mDrawerLayout.openDrawer(GravityCompat.END)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> {
                if(resultCode == RESULT_OK && data != null){
                    try{
                        val bitmap = BitmapFactory.decodeFile(data.getStringExtra("stickerpath"))
                        val sticker = Sticker(bitmap)
                        mStickerLayout.addSticker(sticker)
                    }catch (e:Exception){
                        Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }else if(mStickerLayout.returnAllSticker().size == 0){
                    ifeditingsk(false)
                }else if(resultCode == RESULT_OK){
                    Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}