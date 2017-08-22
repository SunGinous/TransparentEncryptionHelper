package com.example.administrator.myironguard.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.administrator.myironguard.R;

public class EntryActivity extends AppCompatActivity {

    private Handler handler=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler=new Handler();
        /*
         * 隐藏状态栏
         */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_entry);
        /*
         * 状态栏透明
         */
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        entry();
    }

    private void entry(){
        new Thread(){
            @Override
            public void run(){
                super.run();
                try{
                    Thread.sleep(1000);
                    handler.post(runUI);
                    Log.d("TimeInfo","动画没执行");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Runnable runUI=new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setClass(EntryActivity.this,Login_Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0,R.anim.down_from_top);
            Log.d("TimaInfo","动画执行");
        }
    };
}
