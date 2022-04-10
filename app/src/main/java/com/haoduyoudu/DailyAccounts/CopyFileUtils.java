package com.haoduyoudu.DailyAccounts;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CopyFileUtils {
    private static void copyFileUsingChannel(String filepath, String str,String strs) throws IOException {
        try {
            File source = new File(filepath);
            File dest = new File(str+strs);
            FileUtils.makeRootDirectory(filepath);
            FileUtils.makeFilePath(str,strs);
            FileChannel sourceChannel = new FileInputStream(source).getChannel();
            FileChannel destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destChannel.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void CopyFile(String filepath, String str,String strs) {
        try {
            copyFileUsingChannel(filepath,str,strs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
