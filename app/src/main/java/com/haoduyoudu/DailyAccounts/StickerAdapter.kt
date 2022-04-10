package com.haoduyoudu.DailyAccounts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StickerAdapter(val stickerList: List<MySticker>,val context: Context):
    RecyclerView.Adapter<StickerAdapter.ViewHolder>(), View.OnClickListener{

    lateinit var mrecyclerView: RecyclerView

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stickerimage: ImageView = view.findViewById(R.id.stickerimg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stickerlist_item, parent, false)

        val viewHolder = ViewHolder(view)
        view.setOnClickListener(this)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sticker = stickerList[position]
        Glide.with(context)
            .load(sticker.path)
            .into(holder.stickerimage)
    }

    override fun getItemCount() = stickerList.size

    private var onItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onClick(view: View?) {
        //根据RecyclerView获得当前View的位置
        val position = mrecyclerView!!.getChildAdapterPosition(view!!)
        //程序执行到此，会去执行具体实现的onItemClick()方法
        if (onItemClickListener != null) {
            onItemClickListener!!.onItemClick(mrecyclerView, view, position, stickerList.get(position))
        }
    }

    interface OnItemClickListener {
        //参数（父组件，当前单击的View,单击的View的位置，数据）
        fun onItemClick(parent: RecyclerView, view: View?, position: Int, data: MySticker?)
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