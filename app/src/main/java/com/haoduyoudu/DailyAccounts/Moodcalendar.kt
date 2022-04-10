package com.haoduyoudu.DailyAccounts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_moodcalendar.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class Moodcalendar : AppCompatActivity() {

    var Month = 0

    private val calendardata = ArrayList<MyMood>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moodcalendar)
        Month = Calendar.getInstance().get(Calendar.MONTH) + 1
        onleft.addClickScale()
        onright.addClickScale()
        mouth.setText(Month.toString()+"月")
        refreshcalendar(Month)
        onleft.setOnClickListener {
            mouth.setText("加载中")
            Month-=1
            if(Month==0) Month = 12
            refreshcalendar(Month)
            mouth.setText(Month.toString()+"月")

        }
        onright.setOnClickListener {
            mouth.setText("加载中")
            Month+=1
            if(Month==13) Month = 1
            refreshcalendar(Month)
            mouth.setText(Month.toString()+"月")
        }
    }

    fun getMonthDays(year: Int, month: Int): Int {
        return if (month == 2) {
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                29
            } else {
                28
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            30
        } else {
            31
        }
    }
    fun refreshcalendar(m:Int){
        calendardata.clear()
        val a = Calendar.getInstance();
        val nowy = a.get(Calendar.YEAR)
        val nowm = (a.get(Calendar.MONTH) + 1)
        val nowd = a.get(Calendar.DATE)
        val maxday = getMonthDays(nowy,m)
        val fatherpath = "/sdcard/Android/data/com.haoduyoudu.DailyAccounts/"

        for(i in 1..maxday){
            var imageid:Int = 0
            var mooddate:String? = null
            var path:String? = null
            var BT:String? = null
            var isnew = false
            var past = false
            var iswrite = true
            val moodpath=fatherpath+nowy.toString()+
                    if(m.toString().length<2){"0"+m.toString()}
                    else{m.toString()}+
                    if(i.toString().length<2){"0"+i.toString()}
                    else{i.toString()}+
                    "/mood.txt"
            val fody = nowy.toString()+
                        if(m.toString().length<2){"0"+m.toString()}
                        else{m.toString()}+
                        if(i.toString().length<2){"0"+i.toString()}
                        else{i.toString()}
            if(File(moodpath).exists()){
                val moodText:String = FileUtils.readTxtFile(moodpath).toString()
                val moodsplit = moodText.split("$[%|!|%]$")
                if(moodsplit.size==2)
                    imageid = moodsplit[0].toInt()
                else
                    imageid = MyApplication.NumberToMoodImage[moodText.toInt()]!!.toInt() ?: R.mipmap.isnoneface
            }else if(!File(moodpath).exists() && (i==nowd && nowm==m)){
                imageid = R.mipmap.add
                isnew=true
                iswrite = false
            }else{
                BT=i.toString()
                iswrite = false
            }
            if(i==nowd && nowm==m)
                mooddate = "今"
            else
                mooddate = i.toString()
            if((i<nowd && nowm>=m) or (m<=(nowm-1)))
                past = true
            path = fatherpath+nowy.toString()+
                    if(m.toString().length<2){"0"+m.toString()}
                    else{m.toString()}+
                    if(i.toString().length<2){"0"+i.toString()}
                    else{i.toString()}+"/"
            calendardata.add(
                MyMood(mooddate,
                    imageid,
                    path = path,
                    BigText = BT,
                    isnew = isnew,
                    Filenamesofdailyaccounts = fody,
                    past = past,
                    iswrite = iswrite
                )
            )
        }
        val layoutManager = GridLayoutManager(this,5)
        calendar.layoutManager = layoutManager
        val adapter = MoodAdapter(calendardata,this)
        calendar.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if(MyApplication.newwrite){
            mouth.setText("加载中")
            refreshcalendar(Month)
            mouth.setText(Month.toString()+"月")
        }
    }
}
