package com.example.administrator.myironguard.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.myironguard.R;
import com.example.administrator.myironguard.Utils.ListMenuAdapter;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private float nowX;

    private ArrayList<String> mData = new ArrayList<String>(Arrays.asList("修改密码","使用说明","移除root"));
    private ListView listMenu;
    private ListMenuAdapter adapter;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        listMenu = (ListView) findViewById(R.id.list_menu);
        adapter = new ListMenuAdapter(mData, getApplicationContext());
        listMenu.setAdapter(adapter);

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        intent = new Intent(SettingActivity.this, ChangePwdActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.alpha);
                        break;
                    case 1:
                        intent = new Intent(SettingActivity.this, ExplainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.alpha);
                        break;
                    case 2:
                        SharedPreferences.Editor editor=getSharedPreferences("isRooted",MODE_PRIVATE).edit();
                        editor.putString("value","none");
                        editor.apply();
                        Toast.makeText(SettingActivity.this,"Root权限已移除",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent(SettingActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,R.anim.slide_out_left);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                nowX=event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                WindowManager wm=this.getWindowManager();
                float width=(wm.getDefaultDisplay().getWidth())/15;
                if (nowX<width){
                    float deltaX=event.getX()-nowX;
                    if (deltaX>0){
                        Intent intent=new Intent(SettingActivity.this,MainActivity.class);
                        startActivity(intent);
                        SettingActivity.this.finish();
                        overridePendingTransition(0,R.anim.slide_out_right);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
