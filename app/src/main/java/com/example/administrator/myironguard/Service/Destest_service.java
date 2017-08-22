package com.example.administrator.myironguard.Service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.administrator.myironguard.Utils.DESCoder;
import com.example.administrator.myironguard.Utils.InitKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/12/15.
 * 测试DES加密效率的Service
 */
public class Destest_service extends IntentService{

    public Destest_service() {
        super("Destest_service");
    }
    byte[] buff = InitKey.initSecretKey_Des();
    @Override
    protected void onHandleIntent(Intent intent) {
        moveFile1();
    }

    private void moveFile1() {
        String path = null;
        long id;
        int len = -1;
        byte buf[]  = new byte[1024];
        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},null,null,MediaStore.Images.Media._ID + " DESC");
        if (cursor!=null)
        {
            while(cursor.moveToNext())
            {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                id =cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                break;
            }

        }
        String name=path.substring(path.lastIndexOf("/"));
        String newPath = Environment.getExternalStorageDirectory().toString()+ File.separator+this.getPackageName()+File.separator+name;
        String viewPath = Environment.getExternalStorageDirectory().toString()+File.separator+this.getPackageName()+"view"+File.separator+name;
        File filesrc = new File(path);
        File filedes = new File(newPath);
        File fileview = new File(viewPath);
        File dir = new File(fileview.getParent());
        if (!dir.exists())
            dir.mkdirs();
        File dirs = new File(filedes.getParent());
        if (!dirs.exists())
            dirs.mkdirs();
        try {
            FileInputStream fileInputStream = new FileInputStream(filesrc);
//            FileOutputStream fileOutputStream = new FileOutputStream(filedes);
            OutputStream outputStream= DESCoder.encrypt(filedes,buff);
            try {
                while ((len = fileInputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                fileInputStream.close();
                outputStream.close();
                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA+"=?",new String[]{path});
                filesrc.delete();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            InputStream inputStream =DESCoder.decrypt(filedes,buff);
            FileOutputStream fileOutputStream = new FileOutputStream(fileview);
            while((len = inputStream.read(buf)) != -1)
            {
                fileOutputStream.write(buf, 0, len);
            }
            inputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
