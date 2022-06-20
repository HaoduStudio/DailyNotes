package com.haoduyoudu.DailyAccounts

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_showfind.*
import java.io.File

class showfind : AppCompatActivity() {

    lateinit var adapter: TextviewButtonListAdapter
    private val textviewbuttonList = ArrayList<TextviewButtonList>()
    lateinit var y:String
    lateinit var m:String

    lateinit var lastitem:TextviewButtonList
    lateinit var lastitemview:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showfind)


        y = intent.getStringExtra("y").toString()
        m = intent.getStringExtra("m").toString()
        try{
            adapter = TextviewButtonListAdapter(this, R.layout.tewtviewbuttonlistwithnotes_item, textviewbuttonList)
            initTextviewButtonList()
            refreshTBL(adapter)
            listView.adapter = adapter
        }catch (e:Exception){
            e.printStackTrace()
        }
        listView.setOnItemClickListener { _, view, position, _ ->
            val textviewobj = textviewbuttonList[position]
            val image: ImageView = view.findViewById(R.id.ListImage)

            val intent = Intent(this,showdailyaccount::class.java)
            intent.putExtra("path",textviewobj.path)
            intent.putExtra("date",textviewobj.name)
            intent.putExtra("index",textviewobj.index)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try{
                    image.transitionName = "Image"
                    val options = ActivityOptions.makeSceneTransitionAnimation(this, image,"Image")
                    startActivity(intent,options.toBundle())
                }catch (e:Exception){
                    e.printStackTrace()
                    startActivity(intent)
                }
            }else{
                startActivity(intent)
            }
        }
        listView.setOnItemLongClickListener { _, view, position, _ ->
            DeleteFileUtil.delete(File(cacheDir.absolutePath,"shot.jpg").absolutePath)
            FileUtils.savebitmap(rsBlur(this,viewConversionBitmap(f_background)!!,8),cacheDir.absolutePath,"shot.jpg",80)
            lastitem = textviewbuttonList[position]
            lastitemview = view
            if(MyApplication.SHIELD_SHARE_NOTES_ACTON) startActivityForResult(Intent(this,more_ac2::class.java),4)
            else startActivityForResult(Intent(this,more_ac::class.java),4)
            true
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            4 -> if(resultCode == RESULT_OK){
                if(data != null){
                    when(data.getStringExtra("type")){
                        "edit" -> {
                            val image:ImageView = lastitemview.findViewById(R.id.ListImage)

                            val intent = Intent(this,showdailyaccount::class.java)
                            intent.putExtra("path",lastitem.path)
                            intent.putExtra("date",lastitem.name)
                            intent.putExtra("index",lastitem.index)
                            intent.putExtra("rewrite",true)
                            startActivity(intent)
                        }
                        "del" -> {
                            DeleteFileUtil.delete(lastitem.path)
                            initTextviewButtonList()
                            refreshTBL(adapter)
                            Toast.makeText(this,getString(R.string.del_ok), Toast.LENGTH_SHORT).show()
                        }
                        "share" -> {
                            val image:ImageView = lastitemview.findViewById(R.id.ListImage)

                            val intent = Intent(this,showdailyaccount::class.java)
                            intent.putExtra("path",lastitem.path)
                            intent.putExtra("date",lastitem.name)
                            intent.putExtra("index",lastitem.index)
                            intent.putExtra("ac","shot")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                try{
                                    image.transitionName = "Image"
                                    val options = ActivityOptions.makeSceneTransitionAnimation(this, image,"Image")
                                    startActivity(intent,options.toBundle())
                                }catch (e:Exception){
                                    e.printStackTrace()
                                    startActivity(intent)
                                }
                            }else{
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun refreshTBL(adapter: TextviewButtonListAdapter){
        initTextviewButtonList()
        val pathofdailyaccounts = "/sdcard/Android/data/com.haoduyoudu.DailyAccounts/"
        var r:MutableList<String> = mutableListOf()
        for(i in 0..GFN(pathofdailyaccounts).size-1){
            if(m!="00"){
                println("!=00")
                if((GFN(pathofdailyaccounts)[i].substring(0,4) == y) && (GFN(pathofdailyaccounts)[i].substring(4,6) == m)){
                    r.add(0,GFN(pathofdailyaccounts)[i])
                }
            }else{
                println("00")
                if((GFN(pathofdailyaccounts)[i].substring(0,4) == y)) {
                    r.add(0, GFN(pathofdailyaccounts)[i])
                }
            }
        }
        var Filenamesofdailyaccounts = r
        if (Filenamesofdailyaccounts.size != 0) {
            for (i in 0..Filenamesofdailyaccounts.size-1) {
                var name =
                    try {
                        (Filenamesofdailyaccounts[i].substring(0,4) + "-" +  //87
                                Filenamesofdailyaccounts[i].substring(4,6) +
                                "-" +Filenamesofdailyaccounts[i].substring(6,8))
                    }catch (e:Exception){
                        "Nonedate"
                    }
                val moodtext = FileUtils.readTxtFile(pathofdailyaccounts+Filenamesofdailyaccounts[i]+"/"+"mood.txt")
                val moodsplit = moodtext.split("$[%|!|%]$")
                var imageId: Int
                if (moodsplit.size == 2)
                    imageId = moodsplit[0].toInt()
                else
                    imageId = MyApplication.NumberToMoodImage[moodtext.toInt()] ?: R.mipmap.isnoneface

                textviewbuttonList.add(
                    TextviewButtonList(
                        name,
                        imageId,
                        pathofdailyaccounts+Filenamesofdailyaccounts[i]+"/",
                        "DailyAccounts",
                        notes = FileUtils.readTxtFile(pathofdailyaccounts+
                                Filenamesofdailyaccounts[i]+
                                "/week.txt"),
                        index = i))
                Log.d("xxx",FileUtils.readTxtFile(pathofdailyaccounts+ Filenamesofdailyaccounts[i]+ "/week.txt"))
            }
            try{
                Glide.with(this)
                    .load(MyApplication.Mapofweather[MyApplication.weather])
                    .into(img_background)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }else{
            f_title.setBackgroundColor(Color.parseColor("#000000"))
            listView.setBackgroundResource(R.mipmap.weizhaodao)
            img_background.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }
    override fun onResume() {
        super.onResume()
        print("onRestart()")
        if(MyApplication.newwrite){
            print("ref")
            initTextviewButtonList()
            refreshTBL(adapter)

        }
    }
    private fun initTextviewButtonList(){
        textviewbuttonList.clear()
    }
    fun GFN(dirpathx:String):MutableList<String>{
        val fileNames: MutableList<String> = mutableListOf()
        //在该目录下走一圈，得到文件目录树结构
        val fileTree: FileTreeWalk = File(dirpathx).walk()
        fileTree.maxDepth(1) //需遍历的目录层次为1，即无须检查子目录
            .filter { it.isDirectory && it.name != "assest"} //只挑选文件，不处理文件夹
            //.filter { it.extension in listOf("m4a","mp3") }
            .forEach { fileNames.add(it.name) }//循环 处理符合条件的文件
        if(fileNames.size!=0){
            fileNames.removeAt(0)
            fileNames.sort()
            fileNames.reverse()
        }
        return fileNames
    }
    private fun rsBlur(context: Context, source: Bitmap, radius: Int): Bitmap {
        val renderScript = RenderScript.create(context)
        Log.i("blur", "scale size:" + source.width + "*" + source.height)
        val input = Allocation.createFromBitmap(renderScript, source)
        val output = Allocation.createTyped(renderScript, input.type)
        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptIntrinsicBlur.setInput(input)
        scriptIntrinsicBlur.setRadius(radius.toFloat())
        scriptIntrinsicBlur.forEach(output)
        output.copyTo(source)
        renderScript.destroy()
        return source
    }
    fun viewConversionBitmap(v: View,config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
        val w = v.width
        val h = v.height
        val bmp = Bitmap.createBitmap(w, h, config)
        val c = Canvas(bmp)
        /** 如果不设置canvas画布为白色，则生成透明  */
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp
    }
}
