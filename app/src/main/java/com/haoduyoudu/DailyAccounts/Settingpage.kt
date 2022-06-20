package com.haoduyoudu.DailyAccounts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_settingpage.*
import java.io.File

class Settingpage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settingpage)
        val rootdata = "/data/data/com.haoduyoudu.DailyAccounts/"
        val file = File(rootdata+"password.data")

        onopenorclosepassword(file.exists())

        changepassword.setOnClickListener {
            val intent1 = Intent(this,inputpassword::class.java)
            intent1.putExtra("type",MyApplication.RESET_PASSWORD)
            startActivityForResult(intent1,1)
        }
        isopenpassword.setOnToggleChanged { isopen ->
            if(isopen){
                val intent2 = Intent(this,inputpassword::class.java)
                intent2.putExtra("type",MyApplication.SET_PASSWORD)
                startActivityForResult(intent2,2)
            }else{
                val intent3 = Intent(this,inputpassword::class.java)
                intent3.putExtra("type",MyApplication.CLOSE_PASSWORD)
                startActivityForResult(intent3,3)
            }
        }
        openpassword.setOnClickListener {

        }
        dontremenberpass.setOnClickListener {
            val intent4 = Intent(this,forget_password::class.java)
            startActivity(intent4)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> {   //RESET_PASSWORD
                //TODO 没有了
            }
            2 -> {   //SET_PASSWORD
                onopenorclosepassword(resultCode!=RESULT_CANCELED)
            }
            3 -> {
                onopenorclosepassword(resultCode==RESULT_CANCELED)
            }
        }
    }
    fun onopenorclosepassword(yes:Boolean){
        if(yes){
            isopenpassword.setToggleOn()
            textofisopen.text=getString(R.string.opened)
            changepassword.visibility=View.VISIBLE;
            changepassword.addScale(0f,1f)
            dontremenberpass.visibility=View.VISIBLE;
            dontremenberpass.addScale(0f,1f)
        }else{
            isopenpassword.setToggleOff()
            textofisopen.text=getString(R.string.closed)
            changepassword.visibility=View.GONE;
            changepassword.addScale(1f,0f)
            dontremenberpass.visibility=View.GONE;
            dontremenberpass.addScale(1f,0f)
        }

    }
}