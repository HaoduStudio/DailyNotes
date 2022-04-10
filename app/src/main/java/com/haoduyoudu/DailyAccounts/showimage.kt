package com.haoduyoudu.DailyAccounts

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.activity_showimage.*
import java.io.File

class showimage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showimage)
        val path = intent.getStringExtra("imagepath")
        try {
            Glide.with(this)
                .asBitmap()
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.mipmap.loadimage).into(object:BitmapImageViewTarget(imageView){
                    override fun setResource(resource:Bitmap?){
                        super.setResource(resource)
                        if(resource!=null)
                            imageView.setImageBitmap(resource)
                    }
                })

        }catch (e:Exception){
            Toast.makeText(this,"图片出错啦！",Toast.LENGTH_SHORT).show()
            finish()
        }
        imageView.setOnClickListener {
            finish()
        }
    }

}
