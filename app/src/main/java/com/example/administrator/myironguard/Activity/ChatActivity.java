package com.example.administrator.myironguard.Activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myironguard.Bean.Sms;
import com.example.administrator.myironguard.Db.MessageInfo;
import com.example.administrator.myironguard.R;
import com.example.administrator.myironguard.Utils.ChatAdapter;
import com.example.administrator.myironguard.Utils.RecyclerAdapter;
import com.stericson.RootTools.RootTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;
import xander.elasticity.ElasticityHelper;
import xander.elasticity.ORIENTATION;

public class ChatActivity extends AppCompatActivity {
    private float nowX;
    private RecyclerView recyclerView;

    private RecyclerAdapter recyclerAdapter;
    private TextView namebar;
    private TextView numberbar;
    private EditText editText_send;
    private Button button_send;
    private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        namebar=(TextView)findViewById(R.id.name_bar);
        numberbar=(TextView)findViewById(R.id.num_bar);
        Bundle bundle=getIntent().getExtras();
        String name=bundle.getString("contractName");
        String number=bundle.getString("contractNumber");
        namebar.setText(name);
        numberbar.setText(number);

        recyclerView=(RecyclerView)findViewById(R.id.recycleview_message);

        editText_send=(EditText)findViewById(R.id.edit_send);
        button_send=(Button)findViewById(R.id.btn_send);
        button_send.setEnabled(false);

        initMsgs();

        /*
         *监听edittext是否有内容
         */
        editText_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 判断输入不为空，按钮可点击
                if (editText_send.length() != 0) {
                    button_send.setEnabled(true);
                    button_send.setTextColor(getResources().getColor(R.color.colorGreen));
                } else {
                    button_send.setEnabled(false);
                    button_send.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initMsgs() {
        new Thread(){
            @Override
            public void run(){
                super.run();
                handler.post(runUI);
            }
        }.start();
    }

    Runnable runUI=new Runnable() {
        @Override
        public void run() {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            Bundle bundle=getIntent().getExtras();
            String number=bundle.getString("contractNumber");
            final MessageInfo messageInfo=new MessageInfo(getApplicationContext());
            final ArrayList<Sms> msgList=messageInfo.queryByNumber(number);
            recyclerAdapter=new RecyclerAdapter(msgList,getApplicationContext());
            recyclerView.setAdapter(recyclerAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(recyclerView,OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            recyclerAdapter.notifyItemInserted(msgList.size()-1);
            recyclerView.scrollToPosition(msgList.size()-1);
            button_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = editText_send.getText().toString();
                    if (!"".equals(content)) {
                        Log.d("TimeInfo","发送前msglist.size()="+msgList.size());
                        Bundle bundle=getIntent().getExtras();
                        String name=bundle.getString("contractName");
                        String number=bundle.getString("contractNumber");
                        Sms sms=new Sms();
                        long time = System.currentTimeMillis();
                        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH:mm");
                        Date date=new Date(time);
                        sms.date=format.format(date);
                        sms.content=content;
                        sms.type=2;
                        sms.number=number;
                        sms.name=name;
                        messageInfo.save(sms);

                        recyclerAdapter.notifyItemChanged(msgList.size()-1);
                        recyclerView.scrollToPosition(msgList.size()-1);
                        /*
                         *这几句很关键，重新刷新列表加载数据，但是一定要放在上面两句的下面，否则画面会有闪动，体验不好
                         */
                        ArrayList<Sms> msgList=messageInfo.queryByNumber(number);
                        recyclerAdapter=new RecyclerAdapter(msgList,getApplicationContext());
                        recyclerView.setAdapter(recyclerAdapter);
                        editText_send.setText("");

                        /*
                         *发送短信
                         */
                        SmsManager manager=SmsManager.getDefault();
                        /*
                         *一条短信最多70个汉字
                         */
                        if (content.length()<=70){
                            manager.sendTextMessage(number,null,content,null,null);
                            //五个参数分别为：目的号码，短信服务中心号码，内容，短信发送状态的信息（成功或失败），短信是否被对方收到的状态信息
                            /*
                             *发送完毕删除短信
                             */
                            String where="address='"+number+"'";
                            String fullnumber="+86"+number;
                            String fullwhere="address='"+fullnumber+"'";
                            String path = "data/data/com.android.providers.telephony/databases/mmssms.db";
                            try{
                                RootTools.sendShell("chmod 777 " + path, 3000);
                                //SystemManager.RootCommand("chmod 777 " + path); //这种root方法也是可行的
                                SQLiteDatabase database=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);
                                database.delete("sms",where,null);
                                database.delete("sms",fullwhere,null);
                                database.close();
                                RootTools.sendShell("chmod 660 " + path, 3000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            ArrayList<String> list=manager.divideMessage(content);
                            ArrayList<PendingIntent> sentIntents =  new ArrayList<PendingIntent>();
                            Intent intent=new Intent("SENT_SMS_ACTION");
                            PendingIntent sentPI=PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
                            for(int i = 0;i<list.size();i++){
                                sentIntents.add(sentPI);
                            }
                            manager.sendMultipartTextMessage(number, null, list, sentIntents, null);
                            //这五个参数与上面相同，其中发送状态的信息是指发送到运营商（如中国移动）是否成功
                            /*
                             *发送完毕删除短信
                             */
                            String where="address='"+number+"'";
                            String fullnumber="+86"+number;
                            String fullwhere="address='"+fullnumber+"'";
                            String path = "data/data/com.android.providers.telephony/databases/mmssms.db";
                            try{
                                RootTools.sendShell("chmod 777 " + path, 3000);
                                //SystemManager.RootCommand("chmod 777 " + path); //这种root方法也是可行的
                                SQLiteDatabase database=SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);
                                database.delete("sms",where,null);
                                database.delete("sms",fullwhere,null);
                                database.close();
                                RootTools.sendShell("chmod 660 " + path, 3000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    };


    @Override
    public void onBackPressed(){
        Intent intent=new Intent(ChatActivity.this,MessageActivity.class);
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
                        Intent intent=new Intent(ChatActivity.this,MessageActivity.class);
                        startActivity(intent);
                        ChatActivity.this.finish();
                        overridePendingTransition(0,R.anim.slide_out_right);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
