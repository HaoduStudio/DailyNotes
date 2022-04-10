package com.haoduyoudu.DailyAccounts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.get
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import java.io.ByteArrayOutputStream
import java.io.File
import android.widget.AdapterView.OnItemClickListener




class MoreMoodAdapter(val moremoodList: List<MyMoreMood>,val context: Context):
    RecyclerView.Adapter<MoreMoodAdapter.ViewHolder>(),View.OnClickListener{

    var selnum = -1
    lateinit var mrecyclerView: RecyclerView

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moodImage: ImageView = view.findViewById(R.id.ListImage)
        val background: FrameLayout = view.findViewById(R.id.moodbackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.moremoodlist_item, parent, false)

        val viewHolder = ViewHolder(view)
        view.setOnClickListener(this)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mood = moremoodList[position]
        Glide.with(context)
            .load(mood.uri)
            .into(holder.moodImage)
        if (selnum == mood.name.toInt())
            holder.background.visibility = View.VISIBLE
        else
            holder.background.visibility = View.GONE
    }

    override fun getItemCount() = moremoodList.size

    private var onItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onClick(view: View?) {
        //根据RecyclerView获得当前View的位置
        val position = mrecyclerView!!.getChildAdapterPosition(view!!)
        //程序执行到此，会去执行具体实现的onItemClick()方法
        if (onItemClickListener != null) {
            onItemClickListener!!.onItemClick(mrecyclerView, view, position, moremoodList.get(position))
        }
    }

    interface OnItemClickListener {
        //参数（父组件，当前单击的View,单击的View的位置，数据）
        fun onItemClick(parent: RecyclerView, view: View?, position: Int, data: MyMoreMood?)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView!!)
        mrecyclerView = recyclerView!!
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView!!)
        mrecyclerView = null!!
    }


}