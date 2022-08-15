package com.haoduyoudu.DailyAccounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_find.*
import java.util.*

class find : AppCompatActivity(){
    var yt = 0
    var mt = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)
        val a= Calendar.getInstance();
        upforyear.setOnClickListener {
            if(yt == 0){
                yt = a.get(Calendar.YEAR).toInt()
            }else{
                yt+=1
            }
            year.text = yt.toString()
        }
        downforyear.setOnClickListener {
            if(yt == 0){
                yt = a.get(Calendar.YEAR).toInt()
            }else{
                yt-=1
            }
            year.text = yt.toString()
        }
        upformouth.setOnClickListener {
            if(mt == 0){
                mt = (a.get(Calendar.MONTH)+1).toInt()
            }else{
                mt+=1
            }
            if(mt>=13){
                mt = 1
            }
            mouth.text = mt.toString()
        }
        downformouth.setOnClickListener {
            if(mt == 0){
                mt = (a.get(Calendar.MONTH)+1).toInt()
            }else{
                mt-=1
            }
            if(mt<=0){
                mt = 12
            }
            mouth.text = mt.toString()
        }

        button_cancel.setOnClickListener {
            finish()
        }
        button_ok.setOnClickListener {
            if(yt == 0){
                Toast.makeText(this, getString(R.string.find_tips), Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this,showfind::class.java)
                val ms = if(mt.toString().length < 2){
                    "0" + mt.toString()
                }else{
                    mt.toString()
                }
                println(yt.toString())
                println(ms)
                intent.putExtra("y", yt.toString())
                intent.putExtra("m",ms)
                startActivity(intent)
            }
        }
    }
}
