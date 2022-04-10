package com.haoduyoudu.DailyAccounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_select_template.*

class select_template : AppCompatActivity(),View.OnClickListener {

    var rewrite = false

    lateinit var dirname:String
    var nametoimage = HashMap<View,Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_template)
        rewrite=intent.getBooleanExtra("rewrite",false)

        dirname = intent.getStringExtra("path").toString()
        init()

    }
    fun init(){
        for(i in 1..MyApplication.NuberToTemplate.size){
            val button = ImageView(this)
            val lp = LinearLayout.LayoutParams(240, 280)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            button.layoutParams = lp
            button.scaleType = ImageView.ScaleType.FIT_XY

            f_view.addView(button)

            button.addClickScale()
            button.setOnClickListener(this)
            Glide.with(this).load(MyApplication.NuberToTemplate[i]).into(button)
            nametoimage.put(button,i)
        }
    }
    override fun onClick(v: View?) {
        DeleteFileUtil.delete(dirname+"template.data")
        FileUtils.writeTxtToFile(
            (nametoimage.get(v)).toString(),
            dirname,
            "template.data"
        )
        if(rewrite){
            val intent = Intent()
            setResult(RESULT_OK,intent)
        }
        finish()
    }
}
