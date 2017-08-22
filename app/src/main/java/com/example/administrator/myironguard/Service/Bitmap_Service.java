package com.example.administrator.myironguard.Service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.example.administrator.myironguard.Bean.Aes_bean;
import com.example.administrator.myironguard.Db.Mydao;
import com.example.administrator.myironguard.Utils.AESCoder;
import com.example.administrator.myironguard.Utils.InitKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2016/11/26.
 */
public class Bitmap_Service extends IntentService{
    byte[] buff = InitKey.initSecretKey_Aes();
    private Aes_bean mAes_bean=new Aes_bean();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public Bitmap_Service() {
        super("service_bitmap");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            moveFile1();
            Log.d("TimeInfo","存储状态可用");
        }
    }

    public  void  moveFile1()
    {   String path = null;
        long id;
        int len = -1;
        byte buf[]  = new byte[1024];  //byte buf[] 和 byte[] buf是一样的，前一种写法只是为了兼容C语言
        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},null,null,MediaStore.Images.Media._ID + " DESC");
        if (cursor!=null) {
            while(cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                id =cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                Log.d("TimeInfo","获取到照片路径");
                Log.d("TimeInfo","cursor="+cursor);
                cursor.close();
                break;
            }
        }
        String uuid = UUID.randomUUID().toString();
        String encryptName = uuid;
        String name=path.substring(path.lastIndexOf("/"));

        String newPath = Environment.getExternalStorageDirectory().toString()+File.separator+".TransEncryptHelper"+File.separator+encryptName;
        String newPath1=getApplicationContext().getFilesDir().getPath().toString()+File.separator+"scale"+name;
        Log.i("AES_Coder",newPath);
        String viewPath = path;
        File filesrc = new File(path);
        File filedes = new File(newPath);
        File fileview = new File(newPath1);
        File dir = new File(fileview.getParent());
        if (!dir.exists())
            dir.mkdirs();
        File dirs = new File(filedes.getParent());
        if (!dirs.exists())
            dirs.mkdirs();

        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH:mm");
        Date date=new Date(time);
        mAes_bean.key= Base64.encodeToString(buff,Base64.NO_PADDING);
        mAes_bean.src=path;
        mAes_bean.des=newPath;
        mAes_bean.sca=newPath1;       //缩略图路径
        mAes_bean.viewpath=viewPath;
        mAes_bean.name=name;
        mAes_bean.date=format.format(date);
        mAes_bean.type=1;
        Mydao mydao=new Mydao(getApplicationContext());
        mydao.save(mAes_bean);
        try {
            FileInputStream fileInputStream = new FileInputStream(filesrc);
            FileOutputStream fileOutputStream = new FileOutputStream(fileview);
//            BitmapFactory.Options  options = new BitmapFactory.Options();
//            options.inJustDecodeBounds=true;   //不会把图像完全解码，调用decodeFile()得到图像的基本信息
//            BitmapFactory.decodeFile(path,options);
//            double ratio=Math.max(options.outWidth*1.0d/1024f,options.outHeight*1.0f/1024f);
//            options.inSampleSize=(int)Math.ceil(ratio);
////            float realHeight=options.outHeight;
////            float realWidth=options.outWidth;
////            int scale=(int) ((realHeight > realWidth ? realHeight : realWidth) / 100);
////            options.inSampleSize=scale;
//            options.inJustDecodeBounds=false;  //调用decodeFile()得到完整的图像数据
//            Bitmap bitmap =BitmapFactory.decodeFile(path,options);
            //ThumbnailUtils这么好用干嘛还要options设置半天呢，头都疼！！！！
            Bitmap bitmap1=BitmapFactory.decodeFile(path);
            Bitmap bitmap2= ThumbnailUtils.extractThumbnail(bitmap1,150,150);
            bitmap2.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            OutputStream outputStream= AESCoder.encrypt(filedes,buff);
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

        Log.d("TimeInfo","照片转移加密完成");
    }
}
