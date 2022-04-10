package com.haoduyoudu.DailyAccounts

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MoodAdapter(val moodList: List<MyMood>,val context:Context):
    RecyclerView.Adapter<MoodAdapter.ViewHolder>(){
    val moodspath = "/data/data/com.haoduyoudu.DailyAccounts/assest/mood/"
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val moodImage:ImageView = view.findViewById(R.id.ListImage)
        val moodDate:TextView = view.findViewById(R.id.date)
        val BigText:TextView = view.findViewById(R.id.ListBigText)
        val background:FrameLayout = view.findViewById(R.id.background)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.moodlist_item,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.background.setOnClickListener{
            val position = viewHolder.getAdapterPosition()
            val mood = moodList[position]
            val Filenamesofdailyaccounts = mood.Filenamesofdailyaccounts.toString()
            var name =
                try {
                    (Filenamesofdailyaccounts.substring(0,4) + "-" +  //87
                            Filenamesofdailyaccounts.substring(4,6) +
                            "-" +Filenamesofdailyaccounts.substring(6,8))
                }catch (e:Exception){
                    "Nonedate"
                }
            Log.e("test",name)
            if(mood.iswrite && !mood.isnew){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try{
                        viewHolder.moodImage.transitionName = "Image"
                        val options = ActivityOptions.makeSceneTransitionAnimation(context as Activity,viewHolder.moodImage,"Image")
                        val intent = Intent(context, showdailyaccount::class.java)
                        intent.putExtra("path", mood.path)
                        intent.putExtra("date", name)
                        intent.putExtra("rewrite", false)
                        context.startActivity(intent, options.toBundle())
                    }catch (e:Exception){
                        e.printStackTrace()
                        (context as Activity).finish()
                        Toast.makeText(context,"系统有些小错误哦～",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    val intent = Intent(context, showdailyaccount::class.java)
                    intent.putExtra("path", mood.path)
                    intent.putExtra("date", name)
                    intent.putExtra("rewrite", false)
                    context.startActivity(intent)
                }
                MyApplication.newwrite = true
            }else if(mood.isnew && !mood.iswrite){
                val intent = Intent(context,DailyAccounts::class.java)
                context.startActivity(intent)
            }else if(mood.past && !mood.iswrite){ //in past
                val intent = Intent(context,DailyAccounts::class.java)
                intent.putExtra("path", mood.path)
                intent.putExtra("rewrite", true)
                intent.putExtra("old",true)
                intent.putExtra("name",name)
                context.startActivity(intent)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mood = moodList[position]
        if(mood.imageId!=0){
            if (mood.imageId>=1 && mood.imageId<=59)
                Glide.with(context).load(moodspath+mood.imageId+".png").into(holder.moodImage)
            else
                Glide.with(context).load(mood.imageId).into(holder.moodImage)
        }
        holder.moodDate.setText(mood.name)
        if(mood.BigText!=null){
            holder.moodImage.visibility=View.INVISIBLE
            holder.moodDate.visibility=View.INVISIBLE
            holder.BigText.setText(mood.BigText)
        }else{
            holder.BigText.visibility=View.INVISIBLE
        }
    }

    override fun getItemCount() = moodList.size

}