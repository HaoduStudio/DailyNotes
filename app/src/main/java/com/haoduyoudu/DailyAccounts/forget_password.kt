package com.haoduyoudu.DailyAccounts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haoduyoudu.DailyAccounts.MyApplication.Companion.FORGET_PASSWORD_TIME
import kotlinx.android.synthetic.main.activity_forget_password.*
import java.io.File

class forget_password : AppCompatActivity() {
    val rootdata = "/data/data/com.haoduyoudu.DailyAccounts/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        cancel.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_CANCELED,intent)
            finish()
        }
        start.setOnClickListener {
            val file = File(rootdata,"FORGETPASS.dt")
            DeleteFileUtil.delete(file.absolutePath)
            FileUtils.writeTxtToFile((System.currentTimeMillis()+FORGET_PASSWORD_TIME).toString(),rootdata,"FORGETPASS.dt")
            Toast.makeText(this,getString(R.string.setpassword_done),Toast.LENGTH_SHORT).show()
            val intent = Intent()
            setResult(RESULT_OK,intent)
            finish()
        }
    }
}