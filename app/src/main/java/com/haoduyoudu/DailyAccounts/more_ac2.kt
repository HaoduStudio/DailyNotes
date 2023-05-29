package com.haoduyoudu.DailyAccounts

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_more_ac2.*
import java.io.File
import kotlin.concurrent.thread

class more_ac2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_ac2)
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);

        if (File(cacheDir.absolutePath,"shot.jpg").exists())
            Glide.with(this).load(File(cacheDir,"shot.jpg"))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(img_background)

        for (i in arrayListOf<View>(delthis,editthis)){
            i.addClickScale()
            thread {
                runOnUiThread {
                    try {
                        i.addScale(0f,1.2f,100)
                    }catch (e:Exception){}
                }
                Thread.sleep(100)
                runOnUiThread {
                    try {
                        i.addScale(1.2f,1f,50)
                    }catch (e:Exception){}
                }
            }
        }
        delthis.setOnClickListener {
            val intent = Intent()
            intent.putExtra("type","del")
            setResult(RESULT_OK,intent)
            close()
        }
        editthis.setOnClickListener {
            val intent = Intent()
            intent.putExtra("type","edit")
            setResult(RESULT_OK,intent)
            close()
        }
        backthis.setOnClickListener {
            close()
        }
    }
    fun close(){
        f_background.visibility = View.GONE
        thread {
            try {
                Thread.sleep(20)
                finish()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}