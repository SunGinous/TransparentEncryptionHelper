package com.example.administrator.myironguard.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.example.administrator.myironguard.R;

public class ExplainActivity extends AppCompatActivity {

    private float nowX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent(ExplainActivity.this,SettingActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,R.anim.slide_out_right);
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
                        Intent intent=new Intent(ExplainActivity.this,SettingActivity.class);
                        startActivity(intent);
                        ExplainActivity.this.finish();
                        overridePendingTransition(0,R.anim.slide_out_right);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
