package com.haoduyoudu.DailyAccounts

import android.content.Context
import com.hw.videoprocessor.VideoProcessor
import com.hw.videoprocessor.util.VideoProgressListener
import java.io.File

object CompressVideo {
    /**
     * 压缩视频
     *
     * @param mContext
     * @param filepath
     */
    fun compressVideoResouce(mContext: Context?, filepath: String?, despath: String? ,mListener:VideoProgressListener): Boolean {
        try{
            FileUtils.makeRootDirectory(File(despath).parent)
            VideoProcessor.processor(mContext)
                .input(filepath) // .input(inputVideoUri)
                .output(despath)
                .outHeight(160)
                .outWidth(180)
                .bitrate(250*250)       //输出视频比特率
                .frameRate(10)   //帧率
                .iFrameInterval(20)  //关键帧距，为0时可输出全关键帧视频（部分机器上需为-1）
                .progressListener(mListener)
                .process();
            return true
        }catch(e:Exception){
            e.printStackTrace()
            return false
        }
    }
}