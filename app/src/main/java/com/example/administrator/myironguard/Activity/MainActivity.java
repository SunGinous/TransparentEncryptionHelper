package com.example.administrator.myironguard.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myironguard.Db.ContractInfo;
import com.example.administrator.myironguard.Db.Mydao;
import com.example.administrator.myironguard.R;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import android.os.Handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    private Handler handler=null;
    private TextView picNum;
    private TextView contNum;
    private GridView gridView;
    private int[] icon={R.drawable.picture,R.drawable.shoudong,
                        R.drawable.jiamixiaoxi,R.drawable.shezhi};
    private String[] iconName={"查看图片","手动加密","短信加密","设置"};
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        handler=new Handler();

        picNum=(TextView)findViewById(R.id.picnum);
        contNum=(TextView)findViewById(R.id.contnum);
        ContractInfo contractInfo=new ContractInfo(getApplicationContext());
        contNum.setText(contractInfo.checkNum()+"");
        Mydao mydao=new Mydao(getApplicationContext());
        picNum.setText(mydao.checkNum()+"");

        gridView=(GridView)findViewById(R.id.gridview);
        data_list = new ArrayList<Map<String, Object>>();
        getData();
        final String [] from ={"image","text"};
        int [] to = {R.id.image,R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.gridviewitem, from, to);
        gridView.setAdapter(sim_adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        intent=new Intent(MainActivity.this,PictureActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,R.anim.alpha);
                        break;
                    case 1:
                        intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,1);
                        overridePendingTransition(R.anim.dorp_from_top,R.anim.alpha);
                        break;
                    case 2:
                        SharedPreferences preferences = getSharedPreferences("isRooted",MODE_PRIVATE);
                        if (preferences==null){
                            dialog();
                        }else {
                            String value= preferences.getString("value","");
                            if (value.equals("got")){
                                intent = new Intent(MainActivity.this,MessageActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.up_from_down,R.anim.alpha);
                            } else {
                                dialog();
                            }
                        }
                        break;
                    case 3:
                        intent=new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left,R.anim.alpha);
                        break;
                }
            }
        });
    }

    public List<Map<String, Object>> getData(){
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case 1:
                if(data==null){
                    overridePendingTransition(0,R.anim.up_to_top);
                    return;
                }
                if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                    Log.d("TimeInfo","申请权限");
                }else {
                    Uri selectImage = data.getData();
                    Log.d("TimeInfo","uri="+selectImage);
                    final ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    Cursor cursor = contentResolver.query(selectImage, null, null, null, null);
                    if(cursor!=null){
                        Log.d("TimeInfo","cursor1="+cursor);
                        cursor.moveToNext();
                        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        String path = cursor.getString(columnIndex);
                        Log.d("TimeInfo","path="+path);
                        cursor.close();
                        Intent intent = new Intent(MainActivity.this,EncryptActivity.class);
                        intent.putExtra("path",path);
                        startActivity(intent);
                        this.finish();
                        overridePendingTransition(R.anim.slide_in_left,R.anim.alpha);
                        cursor.close();
                    }else {
                        Intent intent = new Intent(MainActivity.this,EncryptActivity.class);
                        String miPath=selectImage.toString().substring(7);
                        intent.putExtra("path",miPath);
                        startActivity(intent);
                        this.finish();
                        overridePendingTransition(R.anim.slide_in_left,R.anim.alpha);
                    }

                }
                break;
            case 3:
                int i=ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(i==PackageManager.PERMISSION_GRANTED){
                    Uri selectImage = data.getData();
                    final ContentResolver contentResolver = MainActivity.this.getContentResolver();
                    Cursor cursor = contentResolver.query(selectImage, null, null, null, null);
                    assert cursor != null;
                    Log.d("TimeInfo","cursor2="+cursor);
                    cursor.moveToNext();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    String path = cursor.getString(columnIndex);
                    cursor.close();
                    Intent intent = new Intent(MainActivity.this,EncryptActivity.class);
                    intent.putExtra("path",path);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,0);
                }else {
                    Toast.makeText(this,"权限获取失败",Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode==2){
            Log.d("TimeInfo","收到请求");
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri=Uri.fromParts("package",getPackageName(),null);
                Log.d("TimeInfo","uri="+uri);
                intent.setData(uri);
                startActivityForResult(intent,3);
            }else {
                Toast.makeText(this,"权限申请成功",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void dialog(){
        final AlertDialog mDialog=new AlertDialog.Builder(MainActivity.this).create();
        mDialog.show();
        mDialog.getWindow().setContentView(R.layout.mydialog);
        mDialog.getWindow().findViewById(R.id.btn_comfirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                        String path = "data/data/com.android.providers.telephony/databases/mmssms.db";
                        try {
                            RootTools.sendShell("chmod 777 " + path, 2000);
                            RootTools.sendShell("chmod 660 " + path, 2000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (RootToolsException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor=getSharedPreferences("isRooted",MODE_PRIVATE).edit();
                        editor.putString("value","got");
                        editor.apply();
                        intent = new Intent(MainActivity.this,MessageActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.up_from_down,R.anim.alpha);
                    }
                });
        mDialog.getWindow().findViewById(R.id.btn_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                    }
                });
    }
}
