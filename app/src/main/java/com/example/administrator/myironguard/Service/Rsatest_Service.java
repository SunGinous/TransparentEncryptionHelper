package com.example.administrator.myironguard.Service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.administrator.myironguard.Utils.InitKey;
import com.example.administrator.myironguard.Utils.RSACoder;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by Administrator on 2016/12/17.
 * 测试RSA加密效率的Service
 */
public class Rsatest_Service extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public Rsatest_Service() {
        super("mytest_Rsa_service");
    }
    KeyPair mKeyPair =InitKey.initSecretKeyPair_Rsa();
    PrivateKey mPrivateKey= InitKey.initPrivateKey_Rsa( mKeyPair);
    PublicKey mPublicKey = InitKey.initPublicKey_Rsa( mKeyPair);
    @Override
    protected void onHandleIntent(Intent intent) {
        moveFile1();
    }
    private void moveFile1() {
        String path = null;
        long id;
        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA}, null, null, MediaStore.Images.Media._ID + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                break;
            }
           cursor.close();
        }
        String name = path.substring(path.lastIndexOf("/"));
        String newPath = Environment.getExternalStorageDirectory().toString() + File.separator + this.getPackageName() + File.separator + name;
        String viewPath = Environment.getExternalStorageDirectory().toString() + File.separator + this.getPackageName() + "view" + File.separator + name;
        File filesrc = new File(path);
        File filedes = new File(newPath);
        File fileview = new File(viewPath);
        File dir = new File(fileview.getParent());
        if (!dir.exists())
            dir.mkdirs();
        File dirs = new File(filedes.getParent());
        if (!dirs.exists())
            dirs.mkdirs();

        RSACoder.encrypt(filesrc, filedes, mPublicKey);
        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{path});
        filesrc.delete();
        RSACoder.decrypt(filedes, fileview, mPrivateKey);

    }
}
