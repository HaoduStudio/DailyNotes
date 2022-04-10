package com.haoduyoudu.DailyAccounts

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_showdailyaccount.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import kotlin.concurrent.thread

class TextviewButtonListAdapter (activity: Activity,val resourceId:Int,data:List<TextviewButtonList>) :
        ArrayAdapter<TextviewButtonList>(activity,resourceId,data){
    val moodspath = "/data/data/com.haoduyoudu.DailyAccounts/assest/mood/"

    inner class ViewHolder(val textviewbuttonlistImage:ImageView,val textviewbuttonlistName:TextView,val textviewbuttonlistImage_video:ImageView?)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View {
        val view:View
        val viewHolder : ViewHolder
        val textviewbuttonlist = getItem(position)
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId,parent,false)
            val textviewbuttonlistName : TextView = view.findViewById(R.id.Buttonname)
            val textviewbuttonlistImage : ImageView = view.findViewById(R.id.ListImage)
            val textviewbuttonlistImage_video : ImageView? = view.findViewById(R.id.ListImage_isvideo)

            viewHolder = ViewHolder(textviewbuttonlistImage,textviewbuttonlistName,textviewbuttonlistImage_video)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        if (textviewbuttonlist != null){
            val roundedCorners = RoundedCorners(DisplayUtil.dip2px(10f))
            val options: RequestOptions = RequestOptions.bitmapTransform(roundedCorners)
            if(textviewbuttonlist.imageId == 0 && textviewbuttonlist.imagepath!=null){
                thread {
                    try{
                        if(textviewbuttonlist.imagepath!!.indexOf("videobuffe/") != -1)
                            viewHolder.textviewbuttonlistImage_video?.post {
                                if(viewHolder.textviewbuttonlistImage_video != null)
                                    viewHolder.textviewbuttonlistImage_video.visibility = View.VISIBLE
                            }
                        viewHolder.textviewbuttonlistImage.post {
                            Glide.with(context).load(textviewbuttonlist.imagepath)
                                .apply(options)
                                .placeholder(R.mipmap.loadimage)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(viewHolder.textviewbuttonlistImage)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    //viewHolder.textviewbuttonlistImage.setImageURI(Uri.parse(textviewbuttonlist.imagepath))
                    println(textviewbuttonlist.imagepath)
                    println("regetview")
                }
            }else if (1 <= textviewbuttonlist.imageId && textviewbuttonlist.imageId <= 59){
                Glide.with(context).load(moodspath+textviewbuttonlist.imageId+".png").into(viewHolder.textviewbuttonlistImage)
            }else{
                Glide.with(context).load(textviewbuttonlist.imageId).into(viewHolder.textviewbuttonlistImage)
            }
            viewHolder.textviewbuttonlistName.text = textviewbuttonlist.name
            if(textviewbuttonlist.notes != ""){
                val textviewbuttonlistNotes : TextView = view.findViewById(R.id.notes)
                textviewbuttonlistNotes.text = textviewbuttonlist.notes
            }

        }
        return view
    }

}