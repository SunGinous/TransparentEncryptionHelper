package com.example.administrator.myironguard.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myironguard.Bean.Aes_bean;
import com.example.administrator.myironguard.Db.ContractInfo;
import com.example.administrator.myironguard.Db.Mydao;
import com.example.administrator.myironguard.R;
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

public class EncryptActivity extends AppCompatActivity {

    private Button btn_encrypt;
    private ImageView imageView;
    byte[] buff = InitKey.initSecretKey_Aes();
    private Aes_bean mAes_bean=new Aes_bean();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        btn_encrypt=(Button)findViewById(R.id.btn_encrypt);
        imageView=(ImageView)findViewById(R.id.image_encrypt);
        final Bundle bundle = getIntent().getExtras();
        final String path = bundle.getString("path");
        Log.d("TimaInfo","接收到path="+path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;   //不会把图像完全解码，调用decodeFile()得到图像的基本信息
        BitmapFactory.decodeFile(path, options);
        double ratio=Math.max(options.outWidth*1.0d/2048f,options.outHeight*1.0f/2048f);
        options.inSampleSize=(int)Math.floor(ratio);
        options.inJustDecodeBounds = false;  //调用decodeFile()得到完整的图像数据
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        imageView.setImageBitmap(bitmap);
        final ContentResolver contentResolver = this.getContentResolver();
        btn_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int len = -1;
                byte buf[] = new byte[1024];
                String uuid = UUID.randomUUID().toString();
                String encryptName = uuid;
                String name = path.substring(path.lastIndexOf("/"));
                String newPath = Environment.getExternalStorageDirectory().toString() + File.separator + ".TransEncryptHelper" + File.separator + encryptName;
                String newPath1 = getApplicationContext().getFilesDir().getPath().toString() + File.separator + "scale" + name;
                Log.i("AES_Coder", newPath);
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
                mAes_bean.key = Base64.encodeToString(buff, Base64.NO_PADDING);
                mAes_bean.src = path;
                mAes_bean.des = newPath;
                mAes_bean.sca = newPath1;       //缩略图路径
                mAes_bean.viewpath = viewPath;
                mAes_bean.name = name;
                mAes_bean.date = format.format(date);
                mAes_bean.type = 2;
                Mydao mydao = new Mydao(getApplicationContext());
                mydao.save(mAes_bean);
                Log.d("TimeInfo","开始加密");
                try {
                    FileInputStream fileInputStream = new FileInputStream(filesrc);
                    FileOutputStream fileOutputStream = new FileOutputStream(fileview);
                    Bitmap bitmap1=BitmapFactory.decodeFile(path);
                    Bitmap bitmap2=ThumbnailUtils.extractThumbnail(bitmap1,150,150);
                    bitmap2.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    OutputStream outputStream = AESCoder.encrypt(filedes, buff);
                    try {
                        while ((len = fileInputStream.read(buf)) != -1) {
                            outputStream.write(buf, 0, len);
                        }
                        fileInputStream.close();
                        outputStream.close();
                        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{path});
                        filesrc.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageView.setVisibility(View.GONE);
                Log.d("TimeInfo", "照片转移加密完成");

                Toast.makeText(getApplicationContext(), "加密成功", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(EncryptActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent(EncryptActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,R.anim.slide_out_left);
    }
}
