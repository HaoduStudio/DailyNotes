package com.haoduyoudu.DailyAccounts


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_daily_accounts.*
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class DailyAccounts : AppCompatActivity(), View.OnClickListener {

    var rewrite = false
    var path = ""

    lateinit var y:String
    lateinit var m:String
    lateinit var d:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_accounts)

        radiobutton1.addClickScale(1.3f)
        radiobutton2.addClickScale(1.3f)
        radiobutton3.addClickScale(1.3f)
        radiobutton4.addClickScale(1.3f)
        radiobutton5.addClickScale(1.3f)
        radiobutton6.addClickScale(1.3f)
        radiobutton7.addClickScale(1.3f)


        radiobutton1.setOnClickListener(this)
        radiobutton2.setOnClickListener(this)
        radiobutton3.setOnClickListener(this)
        radiobutton4.setOnClickListener(this)
        radiobutton5.setOnClickListener(this)
        radiobutton6.setOnClickListener(this)
        radiobutton7.setOnClickListener(this)

        try{
            runOnUiThread {
                Thread.sleep(100)
                radiobutton1.addScale(0.1f,1.0f,800)
                radiobutton2.addScale(0.1f,1.0f,800)
                radiobutton3.addScale(0.1f,1.0f,800)
                radiobutton4.addScale(0.1f,1.0f,800)
                radiobutton5.addScale(0.1f,1.0f,800)
                radiobutton6.addScale(0.1f,1.0f,800)
                radiobutton7.addScale(0.1f,1.0f,800)
            }
        }catch (e:Exception){
            Toast.makeText(this,"系统出了点小问题哦，请稍后再试～",Toast.LENGTH_SHORT).show()
            finish()
        }

        rewrite = intent.getBooleanExtra("rewrite", false)
        if(rewrite){
            if(intent.getStringExtra("path") != null){
                path = intent.getStringExtra("path").toString()
            }else{
                Toast.makeText(this,"系统出了点小问题哦，请稍后再试～",Toast.LENGTH_SHORT).show()
            }
        }
        moremood.setOnClickListener {
            startActivityForResult(Intent(this, MoreMoodsclect::class.java),1)
        }
    }
    override fun onClick(v: View?) {
        val MyMoodNumber = MyApplication.idToMoodNumber[v?.id!!.toInt()] as Int
        savemood(MyMoodNumber.toString())
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        CleanRAMhelper().removeSelfFromParent(radiobutton1)
        CleanRAMhelper().removeSelfFromParent(radiobutton2)
        CleanRAMhelper().removeSelfFromParent(radiobutton3)
        CleanRAMhelper().removeSelfFromParent(radiobutton4)
        CleanRAMhelper().removeSelfFromParent(radiobutton5)
        CleanRAMhelper().removeSelfFromParent(radiobutton6)
        CleanRAMhelper().removeSelfFromParent(radiobutton7)
        System.gc()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val moodnum = data?.getStringExtra("moodnum")
                val moodtext = data?.getStringExtra("text")

                Log.d("DAonActivityResult",moodnum+" "+moodtext+"return !")

                savemood(moodnum+"$[%|!|%]$"+moodtext)

                finish()
            }
        }
    }


    fun savemood(st:String){
        val a = Calendar.getInstance();
        y = a.get(Calendar.YEAR).toString()
        m = (a.get(Calendar.MONTH) + 1).toString()
        d = a.get(Calendar.DATE).toString()
        if (m.length == 1) {
            m = "0" + m
        }
        if (d.length == 1) {
            d = "0" + d
        }

        var dirname = ""
        if (rewrite) {
            dirname = path
        } else {
            dirname = "/sdcard/Android/data/com.haoduyoudu.DailyAccounts/" +
                    y + m + d + "/"
        }


        val dirnamefile = File(dirname)
        val weekDays: Array<String> =
            arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val calendar: Calendar = Calendar.getInstance();
        if(intent.getStringExtra("name") != null){
            Log.d("editdate","true")
            val namedate = intent.getStringExtra("name")!!.split("-")
            calendar.set(namedate[0].toInt(),namedate[1].toInt(),namedate[2].toInt(),0,0,0)
        }

        if ((!dirnamefile.exists()) || (dirnamefile.exists() && rewrite)) {
            print("true")
            DeleteFileUtil.delete(dirname + "mood.txt")
            FileUtils.writeTxtToFile(
                weekDays[calendar!!.get(Calendar.DAY_OF_WEEK) - 1],
                dirname,
                "week.txt"
            )

            FileUtils.writeTxtToFile(
                st,dirname, "mood.txt")

            if (!rewrite || (intent.getBooleanExtra("old",false))) {
                val Filenamesofdailyaccounts = y+m+d
                var name = if(intent.getBooleanExtra("old",false)){
                    intent.getStringExtra("name").toString()
                }
                else (Filenamesofdailyaccounts.substring(0,4) + "-" +
                        Filenamesofdailyaccounts.substring(4,6) +
                        "-" +Filenamesofdailyaccounts.substring(6,8))
                val intent = Intent(this, showdailyaccount::class.java)
                intent.putExtra("rewrite", true)
                intent.putExtra("path",dirname)
                intent.putExtra("date",name)
                startActivity(intent)
            } else {
                val intent = Intent()
                setResult(RESULT_OK,intent)
            }

        } else {
            Toast.makeText(this, "您今天已经写过手帐了\n若想修改和删除,请到\"管理手帐\"界面操作哦", Toast.LENGTH_SHORT).show()
        }
    }
}

