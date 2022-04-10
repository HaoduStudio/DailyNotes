package com.haoduyoudu.DailyAccounts


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sound_recording_dialog.*
import kotlin.concurrent.thread

class sound_recording_dialog : AppCompatActivity() {
    var isRecording = false
    var arm:AudioRecordManager? = null
    lateinit var dirname:String
    lateinit var soundfilename:String
    lateinit var ringReceiver: RINGReceiver

    lateinit var pathx:String

    var isDestroy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_recording_dialog)

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);



        arm = AudioRecordManager()

        val recordtimes = intent.getIntExtra("recordtimes",1)
        val pathx = intent.getStringExtra("path")
        dirname = pathx + "record/"
        soundfilename = recordtimes.toString() + ".pcm"
        startorstop.setOnClickListener {
            if(isRecording){
                try {
                    startorstop.text = "点击录音"
                    textView.text = "录音完成"
                    Toast.makeText(this, "录音完成", Toast.LENGTH_SHORT).show()
                    arm!!.stopRecord()
                    AudioRecordManager.convertPcmToWav(
                        dirname + soundfilename,
                        dirname + soundfilename.replace("pcm", "wav"),
                        8000,
                        1,
                        16
                    )
                    isRecording = false
                }catch (e:Exception){
                    e.printStackTrace()
                }

                val intent = Intent()
                intent.putExtra("type","record")
                setResult(RESULT_OK,intent)
                finish()

            }else{
                arm!!.startRecord(dirname, soundfilename)
                isRecording = true
                startorstop.text = "点击停止"
                textView.text = "正在录音"
                thread {
                    try{
                        for (i in 60 downTo 1){
                            if(!isDestroy) {
                                Thread.sleep(1000)
                                runOnUiThread {
                                    if (i > 3) {
                                        elsetime.setText("还剩${i}秒")
                                    } else {
                                        elsetime.setText("${i}秒后自动暂停")
                                    }
                                }
                                if (i == 1) {
                                    Thread.sleep(1000)
                                    arm!!.stopRecord()
                                    AudioRecordManager.convertPcmToWav(
                                        dirname + soundfilename,
                                        dirname + soundfilename.replace("pcm", "wav"),
                                        8000,
                                        1,
                                        16
                                    )
                                    isRecording = false

                                    val intent = Intent()
                                    intent.putExtra("type", "record")
                                    setResult(RESULT_OK, intent)
                                    finish()

                                }
                            }
                        }
                    }catch (e:Exception){
                        runOnUiThread {
                            Toast.makeText(this,"当前设备忙哦",Toast.LENGTH_SHORT).show()
                        }
                        isRecording = false
                        finish()

                    }
                }
            }
        }
        exit.setOnClickListener {
            finish()
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.xtc.alarmclock.action.ALARM_VIEW_SHOWING")
        intentFilter.addAction("com.xtc.videochat.start")
        intentFilter.addAction("android.intent.action.PHONE_STATE")
        ringReceiver = RINGReceiver()
        registerReceiver(ringReceiver,intentFilter)

    }
    override fun onDestroy() {
        super.onDestroy()
        isDestroy = true
        if(isRecording) {
            arm!!.stopRecord()
            isRecording = false
            DeleteFileUtil.delete(dirname + soundfilename.replace("pcm", "wav"))

            val intent = Intent()
            intent.putExtra("type","record")
            setResult(RESULT_OK,intent)
            finish()

        }
        DeleteFileUtil.delete(dirname + soundfilename)
        arm = null
        unregisterReceiver(ringReceiver)
    }
    inner class RINGReceiver : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("colck","马牛逼！")
            finish()
        }
    }

}
