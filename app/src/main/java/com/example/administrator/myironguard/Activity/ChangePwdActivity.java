package com.example.administrator.myironguard.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.myironguard.R;
import com.example.administrator.myironguard.Utils.MD5Utils;

public class ChangePwdActivity extends AppCompatActivity {

    private EditText editText_old;
    private EditText editText_new;
    private Button btn_sure;

    private float nowX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);


        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        editText_old=(EditText)findViewById(R.id.edit_old);
        editText_new=(EditText)findViewById(R.id.edit_new);
        btn_sure=(Button)findViewById(R.id.btn_sure);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpwd=editText_old.getText().toString();
                String newpwd=editText_new.getText().toString();
                SharedPreferences sharedPreferences=getSharedPreferences("userMessage",MODE_PRIVATE);
                String password=sharedPreferences.getString("userPassword","");
                if (TextUtils.isEmpty(oldpwd)){
                    Toast.makeText(ChangePwdActivity.this,"请输入旧密码",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(newpwd)){
                    Toast.makeText(ChangePwdActivity.this,"请输入新密码",Toast.LENGTH_SHORT).show();
                }else if (!(MD5Utils.MD5(oldpwd).equals(password))){
                    Toast.makeText(ChangePwdActivity.this,"旧密码错误",Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("userMessage",
                            MODE_PRIVATE).edit();
                    editor.putString("userPassword",MD5Utils.MD5(newpwd));
                    editor.apply();

                    Intent intent=new Intent(ChangePwdActivity.this,SettingActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,R.anim.slide_out_right);
                    Toast.makeText(getApplicationContext(),"密码修改成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent(ChangePwdActivity.this,SettingActivity.class);
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
                        Intent intent=new Intent(ChangePwdActivity.this,SettingActivity.class);
                        startActivity(intent);
                        ChangePwdActivity.this.finish();
                        overridePendingTransition(0,R.anim.slide_out_right);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
