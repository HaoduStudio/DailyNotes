package com.haoduyoudu.DailyAccounts

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

public class CopyUtils {
    public fun copyfile(srcFile: String, destDir: String, destFilename: String) {
        //获取要复制的文件
        val oldfile = File(srcFile)
        //文件输入流，用于读取要复制的文件
        val fileInputStream = FileInputStream(oldfile)
        //要生成的新文件（指定路径如果没有则创建）
        val newfile = File(destDir+destFilename)
        //获取父目录
        val fileParent = newfile.parentFile
        System.out.println(fileParent)
        //判断是否存在
        if (!fileParent.exists()) {
            // 创建父目录文件夹
            fileParent.mkdirs()
        }
        //判断文件是否存在
        if (!newfile.exists()) {
            //创建文件
            newfile.createNewFile()
        }

        //新文件输出流
        val fileOutputStream = FileOutputStream(newfile)
        val buffer = ByteArray(1024)
        var len: Int
        //将文件流信息读取文件缓存区，如果读取结果不为-1就代表文件没有读取完毕，反之已经读取完毕
        while (fileInputStream.read(buffer).also { len = it } != -1) {
            fileOutputStream.write(buffer, 0, len)
            fileOutputStream.flush()
        }
        fileInputStream.close()
        fileOutputStream.close()

    }
}