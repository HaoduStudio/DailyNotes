package com.haoduyoudu.DailyAccounts

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import kotlinx.android.synthetic.main.activity_caidan.*

import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.xtc.shareapi.share.communication.BaseResponse
import com.xtc.shareapi.share.communication.SendMessageToXTC
import com.xtc.shareapi.share.communication.ShowMessageFromXTC
import com.xtc.shareapi.share.interfaces.IResponseCallback
import com.xtc.shareapi.share.manager.ShareMessageManager
import com.xtc.shareapi.share.shareobject.XTCAppExtendObject
import com.xtc.shareapi.share.shareobject.XTCShareMessage


class caidan : AppCompatActivity(),IResponseCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caidan)
        val type:String=intent.getStringExtra("type")?:""
        when(type){
            "image" ->{
                page2.visibility=View.GONE
                page1.visibility=View.VISIBLE
                val images = arrayListOf<Int>(R.mipmap.q_aiyu,R.mipmap.q_aiyu2,R.mipmap.q_aiyu3,R.mipmap.q_aiyujieshao,R.mipmap.q_youyujieshao)
                val randomimageindex = (0..4).random()
                when(randomimageindex){
                    0,1,2 -> {Toast.makeText(this,"来看看我们的吉祥物吧!",Toast.LENGTH_SHORT).show()}
                }
                Glide.with(this)
                    .asBitmap()
                    .load(images[randomimageindex])
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.mipmap.loadimage).into(object: BitmapImageViewTarget(imageView){
                        override fun setResource(resource: Bitmap?){
                            super.setResource(resource)
                            if(resource!=null)
                                imageView.setImageBitmap(resource)
                        }
                    })
            }
            "award" ->{
                page2.visibility=View.VISIBLE
                page1.visibility=View.GONE
                share_to_friend.setOnClickListener {
                    val xtcAppExtendObject = XTCAppExtendObject()
                    xtcAppExtendObject.startActivity = MainActivity::class.java.name
                    xtcAppExtendObject.extInfo = ""
                    val xtcShareMessage = XTCShareMessage()
                    xtcShareMessage.shareObject = xtcAppExtendObject
                    xtcShareMessage.setThumbImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    xtcShareMessage.description = "发现一个小彩蛋～"
                    val request = SendMessageToXTC.Request()
                    request.message = xtcShareMessage
                    request.setFlag(1)
                    ShareMessageManager(this).sendRequestToXTC(request, "a81252c4145a48a9a52f0d3015a891d9")
                }
                share_to_friend.addClickScale()
            }
            else -> {}
        }
    }

    override fun onReq(p0: ShowMessageFromXTC.Request?) {
    }

    override fun onResp(isSuccess: Boolean, response: BaseResponse?) {
        if(isSuccess){
            Toast.makeText(this,"分享成功", Toast.LENGTH_SHORT).show()
        }else{
            if(response?.getCode()!=2)
                Toast.makeText(this,"分享失败,错误码${response?.getCode() ?: "None"}", Toast.LENGTH_SHORT).show()
        }
    }
}