package com.haoduyoudu.DailyAccounts

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_more_moodsclect.*
import java.util.ArrayList
import kotlin.concurrent.thread
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class MoreMoodsclect : AppCompatActivity() {
    private val moodsList = ArrayList<MyMoreMood>()
    val mcontext = this
    var selnum = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_moodsclect)

        for(i in 1..59)
            moodsList.add(
                MyMoreMood(
                    i.toString()
                    ,"/data/data/com.haoduyoudu.DailyAccounts/assest/mood/" + i.toString() + ".png"
                )
            )
        val layoutManager = StaggeredGridLayoutManager(4,
        StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        val adapter = MoreMoodAdapter(moodsList,this)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : MoreMoodAdapter.OnItemClickListener {
            override fun onItemClick(
                parent: RecyclerView,
                view: View?,
                position: Int,
                data: MyMoreMood?
            ) {
                thread {
                    Log.d("cancee","OK")
                    runOnUiThread {
                        downview.visibility = View.VISIBLE
                        val mood = data
                        if(selnum!=-1)
                            adapter.notifyItemChanged(selnum-1)
                        selnum = mood!!.name.toInt()
                        adapter.selnum = mood!!.name.toInt()
                        adapter.notifyItemChanged(selnum-1)
                    }
                    Log.d("cancee","OK2")
                    runOnUiThread {
                        scrollToPosition(0,360)
                        Glide.with(mcontext).
                        load("/data/data/com.haoduyoudu.DailyAccounts/assest/mood/"+
                                data!!.name+".png").into(Mycelmood)
                    }
                    Log.d("cancee","OK3")
                }
            }
        })
        (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

        button_cancel.setOnClickListener {
            finish()
        }

        button_ok.setOnClickListener {
            if(moodinput.length() <= 7 && moodinput.length() != 0){
                if(selnum!=-1){
                    val intent =  Intent()
                    intent.putExtra("moodnum",selnum.toString())
                    intent.putExtra("text",moodinput.text.toString())
                    setResult(RESULT_OK,intent)
                    finish()
                }else{
                    Toast.makeText(this,"系统忙，请稍后再试",Toast.LENGTH_SHORT).show()
                }
            }else if (moodinput.length() != 0){
                Toast.makeText(this,"请输入表情名称哦",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"七个字以内哦",Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun scrollToPosition(x:Int,y:Int) {

        val xTranslate:ObjectAnimator = ObjectAnimator.ofInt(scrollview, "scrollX", x);
        val yTranslate:ObjectAnimator = ObjectAnimator.ofInt(scrollview, "scrollY", y);

        val animators = AnimatorSet();
        animators.setDuration(1000);
        animators.playTogether(xTranslate, yTranslate);
        animators.addListener(object :Animator.AnimatorListener{

            override fun onAnimationStart(arg0:Animator) {
                // TODO Auto-generated method stub
            }

            override fun onAnimationRepeat(arg0:Animator) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationEnd(arg0:Animator) {
                // TODO Auto-generated method stub
                runOnUiThread {
                    try{
                        Thread.sleep(500)
                        upview.visibility = View.GONE
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }

            override fun onAnimationCancel(arg0:Animator) {
                // TODO Auto-generated method stub
            }
        });
        animators.start();
    }
}