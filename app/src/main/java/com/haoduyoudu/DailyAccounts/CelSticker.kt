package com.haoduyoudu.DailyAccounts

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_cel_sticker.*
import java.util.ArrayList
import android.view.animation.*
import kotlinx.android.synthetic.main.activity_cel_sticker.recyclerView
import kotlin.concurrent.thread
import android.view.animation.AlphaAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.AnimationSet
import java.io.File


class CelSticker : AppCompatActivity() {
    private val stickersList = ArrayList<MySticker>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cel_sticker)

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);


        close_bt.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_CANCELED,intent)
            closeActivity()
        }

        try {
            for (i in assets.list("sticker")!!){
                stickersList.add(MySticker("/data/data/com.haoduyoudu.DailyAccounts/assest/sticker/" + i.toString()))
            }
            if(File("/data/data/com.haoduyoudu.DailyAccounts/assest/moresticker/").exists()){
                for (i in assets.list("moresticker")!!){
                    stickersList.add(MySticker("/data/data/com.haoduyoudu.DailyAccounts/assest/moresticker/" + i.toString()))
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }


        val layoutManager = StaggeredGridLayoutManager(3,
            StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        val adapter = StickerAdapter(stickersList,this)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : StickerAdapter.OnItemClickListener {
            override fun onItemClick(
                parent: RecyclerView,
                view: View?,
                position: Int,
                data: MySticker?
            ) {
                val intent = Intent()
                if(data != null)
                    intent.putExtra("stickerpath",data.path)
                setResult(RESULT_OK,intent)
                closeActivity()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        try {
            val animSet = AnimationSet(true)
            val sa = ScaleAnimation(
                materialCardView.getWidth() as Float
                        / (materialCardView.getParent() as View).width, 1.0f,
                materialCardView.getHeight() as Float / (materialCardView.getParent() as View).height,
                1.0f, materialCardView.getX() + materialCardView.getWidth() / 2, materialCardView.getY() + materialCardView.getHeight() / 2
            )
            sa.duration = 2000
            val aa = AlphaAnimation(0.2f, 1f)
            aa.duration = 2000
            animSet.addAnimation(sa)
            animSet.addAnimation(aa)
            materialCardView.startAnimation(animSet)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun closeActivity(){
        try {
            val rotateAnimation1 = RotateAnimation(2f, -5f,
                Animation.RELATIVE_TO_PARENT, -0.2f, Animation.RELATIVE_TO_PARENT, 0.5f);
            val alphaAnimation = AlphaAnimation(0.7f, 0.0f);
            val animationSet = AnimationSet(true);
            animationSet.setFillAfter(true);
            animationSet.setDuration(1000);
            animationSet.setStartTime(600);
            animationSet.addAnimation(rotateAnimation1);
            animationSet.addAnimation(alphaAnimation);
            materialCardView.startAnimation(animationSet);
            thread {
                try {
                    Thread.sleep(1000)
                    finish()
                }catch (e:Exception){
                    Log.e("onclose","事务还未完成，close&return!")
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}