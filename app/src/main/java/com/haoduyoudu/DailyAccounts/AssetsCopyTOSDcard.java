package com.haoduyoudu.DailyAccounts;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsCopyTOSDcard {
    Context context;

    public AssetsCopyTOSDcard(Context context) {
        super();
        this.context = context;
    }

    /**
     * @param assetpath  asset下的路径
     * @param SDpath     SDpath下保存路径
     */
    public void AssetToSD(String assetpath,String SDpath ){

        AssetManager asset=context.getAssets();
        //循环的读取asset下的文件，并且写入到SD卡
        String[] filenames=null;
        FileOutputStream out = null;
        InputStream in=null;
        try {
            filenames = asset.list(assetpath);
            if(filenames.length>0){//说明是目录
                //创建目录
                getDirectory(assetpath);

                for(String fileName:filenames){
                    AssetToSD(assetpath+fileName, SDpath+"/"+fileName);
                }
            }else{//说明是文件，直接复制到SD卡
                File SDFlie=new File(SDpath);
                String  path=assetpath.substring(0, assetpath.lastIndexOf("/"));
                getDirectory(path);

                if(!SDFlie.exists()){
                    SDFlie.createNewFile();
                }
                //将内容写入到文件中
                in=asset.open(assetpath);
                out= new FileOutputStream(SDFlie);
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=in.read(buffer))!=-1){
                    out.write(buffer, 0, byteCount);
                }
                out.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                assert out != null;
                out.close();
                in.close();
                asset.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    //分级建立文件夹
    public void getDirectory(String path){
        //对SDpath进行处理，分层级建立文件夹
        String[]  s=path.split("/");
        String str=Environment.getExternalStorageDirectory().toString();
        for (int i = 0; i < s.length; i++) {
            str=str+"/"+s[i];
            File file=new File(str);
            if(!file.exists()){
                file.mkdir();
            }
        }

    }
}