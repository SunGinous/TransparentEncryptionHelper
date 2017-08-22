package com.example.administrator.myironguard.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myironguard.R;

import java.io.File;

/**
 * 如果需要实现“离开软件界面、再切回时首先显示登陆界面”这一功能，请将所有Activity继承该BaseActivity
 */
public class BaseActivity extends AppCompatActivity {

    private boolean ok;
    private Intent intent;
    private Button btn_regist;
    private Button btn_login;
    private EditText passWord;
    private TextView textTip;
    @Override
    public void onStart(){
        if (ok){
            setContentView(R.layout.activity_login);

            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            textTip=(TextView)findViewById(R.id.text_tip);
            btn_regist=(Button) findViewById(R.id.btn_regist);
            btn_login=(Button)findViewById(R.id.btn_login);

            passWord=(EditText)findViewById(R.id.passWord);

            btn_regist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String psword=passWord.getText().toString();
                    if(TextUtils.isEmpty(psword)){
                        Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences.Editor editor = getSharedPreferences("userMessage",
                            MODE_PRIVATE).edit();
                    editor.putString("userPassword",psword);
                    editor.commit();

                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"恭喜您注册成功！",Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_in_top,R.anim.alpha);
                }
            });

            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String psword = passWord.getText().toString();
                    SharedPreferences pref = getSharedPreferences("userMessage",MODE_PRIVATE);
                    final String passsword = pref.getString("userPassword","");
                    if(psword.equals(passsword)){
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_top,R.anim.alpha);
                    }else {
                        Toast.makeText(getApplicationContext(),"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            SharedPreferences preferences = getSharedPreferences("userMessage",MODE_PRIVATE);
            final String name = preferences.getString("userName","");
            File file = new File("/data/data/"+getPackageName().toString()+"/shared_prefs","userMessage.xml");
            if(file.exists()){
                btn_regist.setVisibility(View.GONE);
                textTip.setText("*请输入密码");
            }else {
                btn_login.setVisibility(View.GONE);
                textTip.setText("*输入密码完成注册");
                passWord.setText(null);
            }
        }
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
        ok=true;
    }
}
