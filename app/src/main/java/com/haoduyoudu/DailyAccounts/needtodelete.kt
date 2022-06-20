package com.haoduyoudu.DailyAccounts

import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_needtodelete.*


class needtodelete : AppCompatActivity() {

    var yestodel = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_needtodelete)

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.CENTER);

        val path = intent.getStringExtra("pathname")
        val type = intent.getStringExtra("type")
        val withtodel:String? = intent.getStringExtra("withpath") ?: null
        cancel.setOnClickListener {
            try{
                delete.visibility = View.GONE
                left.visibility = View.GONE
                cancel.visibility = View.GONE
                right.visibility = View.GONE
            }catch (e:Exception){
                e.printStackTrace()
            }
            val intent = Intent()
            intent.putExtra("delete",false)
            intent.putExtra("type",type)
            setResult(RESULT_OK,intent)
            finish()
        }
        delete.setOnClickListener {
            try{
                delete.visibility = View.GONE
                left.visibility = View.GONE
                cancel.visibility = View.GONE
                right.visibility = View.GONE
            }catch (e:Exception){
                e.printStackTrace()
            }
            try {
                val pmu = PlaymediafromresUtils(R.raw.del,this)
                pmu.play()

                val vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(34) //单位是ms
            }catch (e:Exception){
                e.printStackTrace()
            }
            try {
                DeleteFileUtil.delete(path)
                if(withtodel != null){
                    DeleteFileUtil.delete(withtodel)
                }
                Toast.makeText(this,getString(R.string.del_ok),Toast.LENGTH_SHORT).show()
            }catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(this,getString(R.string.del_fail),Toast.LENGTH_SHORT).show()
                yestodel = false

            }
            val intent = Intent()
            intent.putExtra("delete",yestodel)
            intent.putExtra("type",type)
            setResult(RESULT_OK,intent)
            finish()
        }
    }

}
