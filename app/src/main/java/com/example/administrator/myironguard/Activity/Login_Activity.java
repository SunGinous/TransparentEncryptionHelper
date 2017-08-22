package com.example.administrator.myironguard.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myironguard.R;
import com.example.administrator.myironguard.Utils.MD5Utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login_Activity extends AppCompatActivity {

    private Intent intent;
    private Button btn_regist;
    private Button btn_login;
    private EditText passWord;
    private TextView textTip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    Toast.makeText(Login_Activity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                /*
                 *这里使用MD5
                 */
                String pswordMD5= MD5Utils.MD5(psword);
                SharedPreferences.Editor editor = getSharedPreferences("userMessage",
                        MODE_PRIVATE).edit();
                editor.putString("userPassword",pswordMD5);
                editor.apply();

                intent = new Intent(Login_Activity.this,MainActivity.class);
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
                /*
                 *这里使用MD5
                 */
                String tomd5=MD5Utils.MD5(psword);
                if(tomd5.equals(passsword)){
                    Intent intent = new Intent(Login_Activity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_top,R.anim.alpha);
                }else {
                    Toast.makeText(Login_Activity.this,"密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("userMessage",MODE_PRIVATE);
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
}
