package com.haoduyoudu.DailyAccounts

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_edit_daily_text.*
import java.io.File
import kotlin.concurrent.thread

class edit_daily_text : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_daily_text)

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        try{
            inputtext.typeface = Typeface.createFromFile("/system/fonts/DroidSansMono.ttf")
        }catch (e:Exception){
            e.printStackTrace()
        }

        try{
            if (File(cacheDir.absolutePath,"shot.jpg").exists())
                Glide.with(this).load(File(cacheDir,"shot.jpg"))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(img_background)
        }catch (e:Exception){
            e.printStackTrace()
        }
        var textcolor = intent.getIntExtra("color",1)
        upupdatacolor(textcolor)
        colors_cel.visibility = View.GONE
        celcolor.setOnClickListener {
            start()
        }
        try {
            val views = getAllChildViews(colors_cel)
            for(i in 1..views.size){
                views[i-1].setOnClickListener {
                    textcolor = i
                    upupdatacolor(i)
                    end()
                    Log.d("edittext","change color")
                }
                views[i-1].addClickScale()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        try{
            inputtext.setText(intent.getStringExtra("text")!!.replace("\r",""))
        }catch (e:Exception){
            e.printStackTrace()
        }

        quit.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_CANCELED,intent)
            finish()
        }
        ok.setOnClickListener {
            val intent = Intent()
            intent.putExtra("newtext",inputtext.text.toString())
            intent.putExtra("newcolor",textcolor)
            setResult(RESULT_OK,intent)
            finish()
        }
        enter.setOnClickListener {
            try{
                val lastsele = inputtext.selectionStart
                Log.d("textedit",lastsele.toString())
                Log.d("textedit","textedit from char (0 ... ${lastsele+1}) ")
                Log.d("textedit","textedit from char (${lastsele} ... ${inputtext.length()}) ")
                if(inputtext.length() != lastsele) inputtext.setText(inputtext.text.subSequence(0,lastsele).toString()+
                        "\n"
                        +inputtext.text.subSequence(lastsele,inputtext.length()).toString())
                else inputtext.setText(inputtext.text.toString()+"\n")
                inputtext.setSelection(lastsele+1)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    private fun getAllChildViews(view: View?): List<View> {
        val allChildViews: MutableList<View> = ArrayList()
        if (view != null && view is ViewGroup) {
            val vp = view
            for (i in 0 until vp.childCount) {
                val viewChild = vp.getChildAt(i) as ViewGroup
                allChildViews.add(viewChild.getChildAt(0))
                allChildViews.add(viewChild.getChildAt(1))
                allChildViews.add(viewChild.getChildAt(2))
            }
        }
        return allChildViews
    }
    private fun start(){
        val views = getAllChildViews(colors_cel)
        colors_cel.visibility = View.GONE
        for(i in views){runOnUiThread { i.setScale(0f) }}
        colors_cel.visibility = View.VISIBLE
        for(i in 1..views.size){
            thread {
                try{
                    Thread.sleep(i*80L)
                    runOnUiThread { views[i-1].addScale(0f,1f,200) }
                }catch (e:Exception){e.printStackTrace()}
            }
        }
    }
    private fun end(){
        val views = getAllChildViews(colors_cel)
        for(i in 1..views.size){
            thread {
                try{
                    thread {
                        try {
                            Thread.sleep(80L+i*50L)
                            runOnUiThread { views[i-1].addScale(1f,0f,200) }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }catch (e:Exception){e.printStackTrace()}
                Thread.sleep(100L+700L)
                runOnUiThread { colors_cel.visibility = View.GONE }
            }
        }
    }
    private fun upupdatacolor(color:Int){
        runOnUiThread {
            inputtext.setTextColor(resources.getColor(MyApplication.nuberToTextColor[color]!!))
        }
    }
}