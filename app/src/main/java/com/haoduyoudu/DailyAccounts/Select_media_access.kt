package com.haoduyoudu.DailyAccounts

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.MAX_IMAGE_OR_VIDEO_COUNT
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.MAX_RECORD_COUNT
import kotlinx.android.synthetic.main.activity_select_media_access.*
import java.io.File
import kotlin.concurrent.thread


class Select_media_access : AppCompatActivity(),View.OnClickListener {
    //水代码「呵呵」

    var imagetimes:Int = 0
    var videotimes:Int = 0
    var recordtimes:Int = 0
    val fromAlbum = 1
    val takePhoto = 2
    val takeVideo = 3

    var cantouch = true


    var pathx = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_media_access)

        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //getWindow().setGravity(Gravity.CENTER);

        videotimes=intent.getIntExtra("videotimes",1)
        imagetimes=intent.getIntExtra("imagetimes",1)
        recordtimes=intent.getIntExtra("recordtimes",1)
        pathx = intent.getStringExtra("path").toString()

        if (File(cacheDir.absolutePath,"shot.jpg").exists())
            Glide.with(this).load(File(cacheDir.absolutePath,"shot.jpg")).diskCacheStrategy(
                DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(background_img)

        thread {
            val SB = StringBuilder()
            var i = 0
            while (true){
                Thread.sleep(1000)
                try {
                    SB.clear()
                    SB.append(getString(R.string.selmedia_loading))
                    i++
                    repeat(i%3+1){
                        SB.append(".")
                    }
                    runOnUiThread {
                        Loading.text = SB.toString()
                    }
                }catch (e: Exception){
                    println("ok")
                }
            }
        }

        fromalbum.visibility = View.INVISIBLE
        takephoto.visibility = View.INVISIBLE
        takevideo.visibility = View.INVISIBLE
        recordthis.visibility = View.INVISIBLE

        fromalbum.addClickScale()
        takephoto.addClickScale()
        takevideo.addClickScale()
        recordthis.addClickScale()

        fromalbum.setOnClickListener(this)
        takephoto.setOnClickListener(this)
        takevideo.setOnClickListener(this)
        recordthis.setOnClickListener(this)
        thread {
            try {
                Thread.sleep(600)
                runOnUiThread {
                    fromalbum.visibility = View.VISIBLE
                    takephoto.visibility = View.VISIBLE
                    takevideo.visibility = View.VISIBLE
                    recordthis.visibility = View.VISIBLE
                    fromalbum.addScale(0f,1f)
                    takephoto.addScale(0f,1f)
                    takevideo.addScale(0f,1f)
                    recordthis.addScale(0f,1f)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cantouch = false
        try {
            celmedia.visibility = View.GONE
            lodings.setBackgroundColor(Color.parseColor("#ECE5D1"));
            lodings.visibility = View.VISIBLE
            Glide.with(this).load(R.mipmap.loadinggif).into(progressBar)
            when (requestCode) {
                fromAlbum -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val bundle: Bundle? = data?.getExtras();
                        //获取文件路径和类型
                        if (bundle != null) {
                            //获取文件路径
                            val photoPath: String = bundle.getString(MediaStore.EXTRA_OUTPUT, null)
                            //获取文件类型，0代表图片，1代表视频
                            val type: Int = bundle.getInt("com.xtc.camera.EXTRA_PHOTO_TYPE")
                            val path = pathx +
                                    when (type) {
                                        0 -> "image/"
                                        1 -> "video/"
                                        else -> ""
                                    }
                            val videobuffe = pathx + "videobuffe/"
                            val pathname =
                                when (type) {
                                    0 -> imagetimes.toString()
                                    1 -> videotimes.toString()
                                    else -> ""
                                } + when (type) {
                                    0 -> ".jpg"
                                    1 -> ".mp4"
                                    else -> ""
                                }
                            Log.d("相册photo", photoPath)
                            Log.d("目的地", path)
                            thread {
                                try{
                                    if(type == 0){
                                        CopyFileUtils.CopyFile(photoPath, path, pathname)
                                    }else{
                                        val result = CompressVideo.compressVideoResouce(this,photoPath,path+pathname,
                                            { progress ->  Log.d("压缩视频","进度${progress}")})
                                        if(!result)
                                            runOnUiThread {
                                                Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }catch (e:Exception){
                                    Log.e("copy","复制图片失败")
                                    e.printStackTrace()
                                    runOnUiThread {
                                        Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
                                    }
                                }
                                try {
                                    if (type == 1) {
                                        var mmr = MediaMetadataRetriever()
                                        mmr.setDataSource(photoPath)
                                        FileUtils.savebitmap(
                                            mmr.getFrameAtTime(),
                                            videobuffe,
                                            videotimes.toString() + ".jpg",
                                            60
                                        )
                                        mmr.release()
                                    }
                                    Thread.sleep(1000)
                                }catch (e:Exception){
                                    try{
                                        var bitmapofvideobuffe:Bitmap? = null
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            val vectorDrawable: Drawable = this.getDrawable(R.mipmap.novideo)!!
                                            bitmapofvideobuffe = Bitmap.createBitmap(
                                                vectorDrawable.intrinsicWidth,
                                                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                                            )
                                            val canvas = Canvas(bitmapofvideobuffe)
                                            vectorDrawable.setBounds(
                                                0,
                                                0,
                                                canvas.getWidth(),
                                                canvas.getHeight()
                                            )
                                            vectorDrawable.draw(canvas)
                                        }else{
                                            val options: BitmapFactory.Options = BitmapFactory.Options();
                                            options.inJustDecodeBounds =true;
                                            bitmapofvideobuffe = BitmapFactory.decodeResource(getResources(), R.mipmap.novideo, options)
                                        }
                                        FileUtils.savebitmap(
                                            bitmapofvideobuffe,
                                            videobuffe,
                                            videotimes.toString() + ".jpg",
                                            60
                                        )
                                        Thread.sleep(200)
                                    }catch (e:Exception){
                                        runOnUiThread {
                                            Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    e.printStackTrace()
                                }finally {
                                    finish()
                                }

                            }

                            val intent = Intent()
                            intent.putExtra(
                                "type", when (type) {
                                    0 -> {
                                        println("back image")
                                        "image"
                                    }
                                    1 -> {
                                        println("back video")
                                        "video"
                                    }
                                    else -> ""
                                }
                            )
                            setResult(RESULT_OK, intent)
                            Log.d("Sma", "返回数据")

                        }
                    }
                }
                takePhoto -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val bundle: Bundle? = data?.getExtras();
                        //获取文件路径和类型
                        if (bundle != null) {
                            //获取文件路径
                            val photoPath: String = bundle.getString(MediaStore.EXTRA_OUTPUT, null)
                            //获取文件类型，0代表图片，1代表视频
                            val path = pathx + "image/"
                            val pathname = imagetimes.toString() + ".jpg"
                            thread {
                                CopyFileUtils.CopyFile(photoPath, path, pathname)
                                Thread.sleep(1000)
                                finish()
                            }

                            val intent = Intent()
                            intent.putExtra("type", "image")
                            setResult(RESULT_OK, intent)
                        }
                    }
                }
                takeVideo -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val bundle: Bundle? = data?.getExtras();
                        //获取文件路径和类型
                        if (bundle != null) {
                            //获取文件路径
                            val videoPath: String = bundle.getString(MediaStore.EXTRA_OUTPUT, null)
                            //获取文件类型，0代表图片，1代表视频
                            val path = pathx + "video/"
                            val videobuffe = pathx + "videobuffe/"
                            val pathname = videotimes.toString() + ".mp4"
                            thread {
                                try{
                                    val result = CompressVideo.compressVideoResouce(this,videoPath,path+pathname,
                                        { progress ->  Log.d("压缩视频","进度${progress}")})
                                    if(!result)
                                        runOnUiThread {
                                            Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
                                        }
                                }catch (e:Exception){
                                    Log.e("copy","复制图片失败")
                                    e.printStackTrace()
                                    runOnUiThread {
                                        Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
                                    }
                                }
                                try {
                                    var mmr = MediaMetadataRetriever()
                                    mmr.setDataSource(videoPath)
                                    FileUtils.savebitmap(
                                        mmr.getFrameAtTime(),
                                        videobuffe,
                                        videotimes.toString() + ".jpg",
                                        60
                                    )
                                    mmr.release()
                                    Thread.sleep(1000)
                                }catch (e:Exception){
                                    var bitmapofvideobuffe:Bitmap? = null
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        val vectorDrawable: Drawable = this.getDrawable(R.mipmap.novideo)!!
                                        bitmapofvideobuffe = Bitmap.createBitmap(
                                            vectorDrawable.intrinsicWidth,
                                            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                                        )
                                        val canvas = Canvas(bitmapofvideobuffe!!)
                                        vectorDrawable.setBounds(
                                            0,
                                            0,
                                            canvas.getWidth(),
                                            canvas.getHeight()
                                        )
                                        vectorDrawable.draw(canvas)
                                    }else{
                                        val options: BitmapFactory.Options = BitmapFactory.Options();
                                        options.inJustDecodeBounds =true;
                                        bitmapofvideobuffe = BitmapFactory.decodeResource(getResources(), R.mipmap.novideo, options)
                                    }
                                    FileUtils.savebitmap(
                                        bitmapofvideobuffe,
                                        videobuffe,
                                        videotimes.toString() + ".jpg",
                                        60
                                    )
                                    Thread.sleep(200)
                                    e.printStackTrace()
                                }finally {
                                    finish()
                                }
                            }
                            val intent = Intent()
                            intent.putExtra("type", "video")
                            setResult(RESULT_OK, intent)
                        }
                    }
                }
            }
        }catch (e:Exception){
            Toast.makeText(this,getString(R.string.add_fail),Toast.LENGTH_SHORT).show()
            val pathofimage = pathx+ "/image/"
            val pathofvideo = pathx+ "/video/"
            //如果有的话
            DeleteFileUtil.delete(pathofimage+imagetimes.toString()+".jpg")
            DeleteFileUtil.delete(pathofvideo+videotimes.toString()+".mp4")
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
        if(resultCode != Activity.RESULT_OK){
            val intent = Intent()
            setResult(RESULT_CANCELED, intent)
            finish()
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (cantouch) {
            super.dispatchTouchEvent(ev)
        } else {
            false
        }

    }

    override fun onClick(v: View?) {
        if(v!=null){
            val listofview = arrayListOf<Int>(fromalbum.id,takephoto.id,takevideo.id)
            if (listofview.indexOf(v.id) != -1){
                Log.e("imagetimes","$imagetimes")
                Log.e("videotimes","$videotimes")
                if((imagetimes-1) + (videotimes-1) >= MAX_IMAGE_OR_VIDEO_COUNT){
                    Toast.makeText(this, getString(R.string.selmedia_max_imgorvid_count,MAX_IMAGE_OR_VIDEO_COUNT), Toast.LENGTH_SHORT).show()
                }else{
                    when(v.id){
                        fromalbum.id->{
                            try {
                                val intent= Intent()
                                intent.setAction(Intent.ACTION_GET_CONTENT)
                                intent.setType("file/*")
                                intent.putExtra("com.xtc.camera.LEFT_BUTTON_TEXT",getString(R.string.cancel))
                                intent.putExtra("com.xtc.camera.RIGHT_BUTTON_TEXT",getString(R.string.ok))
                                startActivityForResult(intent,fromAlbum)
                            }catch (e:Exception){
                                val intent = Intent()
                                setResult(RESULT_CANCELED, intent)
                                e.printStackTrace()
                                Toast.makeText(this,getString(R.string.model_not_support),Toast.LENGTH_LONG).show()
                                finish()
                            }
                        }
                        takephoto.id->{
                            try {
                                val intent: Intent = Intent()
                                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE)
                                intent.putExtra("com.xtc.camera.LEFT_BUTTON_TEXT", getString(R.string.cancel))
                                intent.putExtra("com.xtc.camera.RIGHT_BUTTON_TEXT", getString(R.string.ok))
                                startActivityForResult(intent, takePhoto)
                            }catch (e:Exception){
                                val intent = Intent()
                                setResult(RESULT_CANCELED, intent)
                                e.printStackTrace()
                                Toast.makeText(this,getString(R.string.model_not_support),Toast.LENGTH_LONG).show()
                                finish()
                            }
                        }
                        takevideo.id->{
                            try {
                                val intent: Intent = Intent()
                                intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE)
                                intent.putExtra("com.xtc.camera.LEFT_BUTTON_TEXT",getString(R.string.cancel))
                                intent.putExtra("com.xtc.camera.RIGHT_BUTTON_TEXT",getString(R.string.ok))
                                startActivityForResult(intent,takeVideo)
                            }catch (e:Exception){
                                val intent = Intent()
                                setResult(RESULT_CANCELED, intent)
                                e.printStackTrace()
                                Toast.makeText(this,getString(R.string.model_not_support),Toast.LENGTH_LONG).show()
                                finish()
                            }
                        }
                    }
                }
            }else{
                if((recordtimes-1) >= MAX_RECORD_COUNT){
                    Toast.makeText(this, getString(R.string.selmedia_max_record_count,MAX_RECORD_COUNT), Toast.LENGTH_SHORT).show()
                }else{
                    val intent = Intent()
                    intent.putExtra("type","backrecord")
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
}
