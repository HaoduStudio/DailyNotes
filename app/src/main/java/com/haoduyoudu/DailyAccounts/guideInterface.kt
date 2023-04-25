package com.haoduyoudu.DailyAccounts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_guide_interface.*

class guideInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_interface)
        fadein(mf2,200)
        try {
            FileUtils.writeTxtToFile("",filesDir.path,"NOTNEW.dt")
        }catch (e:Exception){
            e.printStackTrace()
        }
        iseeOK.setOnClickListener {
            finish()
        }
    }
    private fun fadein(v: View, duration:Long = 1500L){
        v.setAlpha(0f);
        v.setVisibility(View.VISIBLE);
        v.animate()
            .alpha(1f)
            .setDuration(duration)
            .setListener(null);
    }
}