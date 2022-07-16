package com.haoduyoudu.DailyAccounts


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_daily_accounts.*
import java.io.File
import java.util.*
import kotlin.Exception


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
            Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
            finish()
        }

        rewrite = intent.getBooleanExtra("rewrite", false)
        if(rewrite){
            if(intent.getStringExtra("path") != null){
                path = intent.getStringExtra("path").toString()
            }else{
                Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
            }
        }
        moremood.setOnClickListener {
            startActivityForResult(Intent(this, MoreMoodsclect::class.java),1)
        }
    }
    override fun onClick(v: View?) {
        try {
            val MyMoodNumber = MyApplication.idToMoodNumber[v?.id!!.toInt()] as Int
            savemood(MyMoodNumber.toString())
            finish()
        }catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
        }
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
                try {
                    val moodnum = data?.getStringExtra("moodnum")
                    val moodtext = data?.getStringExtra("text")

                    Log.d("DAonActivityResult",moodnum+" "+moodtext+"return !")

                    savemood(moodnum+"$[%|!|%]$"+moodtext)

                    finish()
                }catch (e:Exception){
                    e.printStackTrace()
                    Toast.makeText(this,getString(R.string.system_error),Toast.LENGTH_SHORT).show()
                }
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
        val mwek = if(intent.getStringExtra("name") != null){
            val namedate = intent.getStringExtra("name")!!.split("-")
            getDate(namedate[0].toInt(),namedate[1].toInt(),namedate[2].toInt())
        }else{
            getDate(y.toInt(), m.toInt(), d.toInt())
        }

        if ((!dirnamefile.exists()) || (dirnamefile.exists() && rewrite)) {
            print("true")
            DeleteFileUtil.delete(dirname + "mood.txt")
            FileUtils.writeTxtToFile(
                weekDays[mwek],
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
            Toast.makeText(this, getString(R.string.sel_mood_tips), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDate(ly: Int, lm: Int, ld: Int):Int{ //栓Q XC I Love You ～～
        val ly2 = if(lm < 3) ly-1 else ly
        val c = ly2.toString().subSequence(0,2).toString().toInt()
        val y = ly2.toString().subSequence(2,4).toString().toInt()
        val m = if(lm < 3) 12+lm else lm
        val d = ld
        return (y+(y/4)+(c/4)-2*c+(26*(m+1)/10)+d-1)%7
    }
}

