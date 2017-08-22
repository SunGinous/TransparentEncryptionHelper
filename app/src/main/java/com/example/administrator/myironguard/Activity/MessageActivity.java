package com.example.administrator.myironguard.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myironguard.Bean.Sms;
import com.example.administrator.myironguard.Db.ContractInfo;
import com.example.administrator.myironguard.Db.MessageInfo;
import com.example.administrator.myironguard.R;
import com.example.administrator.myironguard.Service.DeleteMsgService;
import com.example.administrator.myironguard.Service.MessageService;
import com.example.administrator.myironguard.Service.NotifyService;
import com.example.administrator.myironguard.Utils.MessageAdapter;
import com.example.administrator.myironguard.Utils.SuUtils;
import com.example.administrator.myironguard.Utils.SystemManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class MessageActivity extends AppCompatActivity {
    private Button btn_add;
    private ListView smslistView;
    private MessageAdapter messageAdapter;
    private Sms sms=new Sms();
    private Handler handler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private float nowY;
    private long nowTime;
    private float nowX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        btn_add=(Button)findViewById(R.id.btn_add);

        smslistView=(ListView)findViewById(R.id.lv_sms);
        OverScrollDecoratorHelper.setUpOverScroll(smslistView);
        handler=new Handler();
        newListview();
        AddContract();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Uri uri = Uri.parse("content://contacts/people");这个不行得用下面这个
                Intent intent=new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
                //intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent,4);
            }
        });
    }

    private void AddContract(){
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
            final ContractInfo contractInfo=new ContractInfo(getApplicationContext());
            final ArrayList<Sms> arrayList=contractInfo.query();
            Collections.reverse(arrayList);
            messageAdapter=new MessageAdapter(arrayList,getApplicationContext());
            smslistView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();
            smslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        ContractInfo contractInfo=new ContractInfo(getApplicationContext());
                        ArrayList<Sms> arrayList=contractInfo.query();
                        Collections.reverse(arrayList);
                        Sms sms=arrayList.get(position);
                        Intent intent = new Intent(MessageActivity.this,ChatActivity.class);
                        Log.d("TimeInfo",position+"");
                        intent.putExtra("contractName",sms.name);
                        Log.d("TimeInfo","对话的联系人名为"+sms.name);
                        intent.putExtra("contractNumber",sms.number);
                        Log.d("TimeInfo","联系人的号码为"+sms.number);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,R.anim.alpha);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"请返回重试",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            smslistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                    final AlertDialog alertDialog=new AlertDialog.Builder(MessageActivity.this).create();
                    alertDialog.show();
                    alertDialog.getWindow().setContentView(R.layout.dialog_delete2);
                    alertDialog.getWindow().findViewById(R.id.btn_no2)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.cancel();
                                }
                            });
                    alertDialog.getWindow().findViewById(R.id.btn_yes2)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.cancel();
                                    try{
                                        Animation.AnimationListener al = new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationEnd(Animation arg0) {
                                            }
                                            @Override public void onAnimationRepeat(Animation animation) {}
                                            @Override public void onAnimationStart(Animation animation) {}
                                        };
                                        collapse(view, al);
                                        Sms sms=arrayList.get(position);
                                        new Thread(){
                                            @Override
                                            public void run(){
                                                super.run();
                                                try{
                                                    Thread.sleep(500);
                                                    arrayList.remove(position);
                                                    handler.post(runUI);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }.start();
                                        MessageInfo messageInfo=new MessageInfo(getApplicationContext());
                                        messageInfo.delete(sms);
                                        contractInfo.delete(sms);
                                        messageAdapter.notifyDataSetChanged();
                                        Toast.makeText(MessageActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                    }catch (Exception e){
                                        Toast.makeText(MessageActivity.this,"操作太快",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    return true;
                }
            });
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case 4:
                if(data==null){
                    return;
                }
                Uri selectContact=data.getData();
                Log.d("TimeInfo","selectContract1111="+selectContact);
                ContentResolver contentResolver = MessageActivity.this.getContentResolver();
                Cursor cursor = contentResolver.query(selectContact,null,null,null,null);
                if(cursor!=null){
                    cursor.moveToFirst();
                    int columnIndexName=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    String contactName=cursor.getString(columnIndexName);
                    Log.d("TimeInfo","联系人名："+contactName);
                    String ContactID=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor cursorPhone=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+ContactID,null,null);
                    Log.d("TimeInfo","cursorPhone="+cursorPhone);
                    if(cursorPhone!=null) {
                        cursorPhone.moveToFirst();
                        String contactPhone = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        MessageInfo messageInfo=new MessageInfo(getApplicationContext());
                        ContractInfo contractInfo=new ContractInfo(getApplicationContext());
                        if (contactPhone.charAt(0)=='+'){
                            String phone=contactPhone.substring(3);
                            if (messageInfo.checkExist(phone)){
                                Toast.makeText(MessageActivity.this,"该联系人已存在",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            sms.name=contactName;
                            sms.number=phone;
                            messageInfo.save(sms);
                            contractInfo.save(sms);
                        }else if(messageInfo.checkExist(contactPhone)){
                            Toast.makeText(MessageActivity.this,"该联系人已存在",Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            sms.name=contactName;
                            sms.number=contactPhone;
                            messageInfo.save(sms);
                            contractInfo.save(sms);
                        }
                    }
                    newListview();
                    cursorPhone.close();
                    cursor.close();
                }
                break;
            default:
                break;
        }
    }

    public void newListview(){
        ContractInfo contractInfo=new ContractInfo(getApplicationContext());
        ArrayList<Sms> arrayList=contractInfo.query();
        Collections.reverse(arrayList);
        MessageAdapter messageAdapter2=new MessageAdapter(arrayList,getApplicationContext());
        smslistView.setAdapter(messageAdapter2);
        messageAdapter2.notifyDataSetChanged();
    }

    /*
     *这里有list view，不能用onTouchEvent判断触控
     *用dispatchTouchEvent可以
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!(smslistView.isLongClickable())){
                    smslistView.setLongClickable(true);
                }
                if(!(smslistView.isClickable())){
                    smslistView.setClickable(true);
                }
                if(!(smslistView.isFocusable())){
                    smslistView.setFocusable(true);
                }
                nowY = event.getY();
                nowX = event.getX();
                Log.d("TimeInfo","nowY1="+nowY);
                nowTime=System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY=event.getY()-nowY;
                Log.d("TimeInfo","deltaY="+deltaY);
                if (deltaY!=0){
                    smslistView.setFocusable(false);
                    smslistView.setClickable(false);
                    smslistView.setLongClickable(false);
                    Log.d("TimeInfo","滑动后点击无效执行");
                }
                WindowManager wm = this.getWindowManager();
                float width = (wm.getDefaultDisplay()).getWidth() / 15;
                if(nowX < width){
                    float deltaX = event.getX() - nowX;
                    if(deltaX > 0){
                        Intent intent=new Intent(MessageActivity.this,MainActivity.class);
                        startActivity(intent);
                        MessageActivity.this.finish();
                        overridePendingTransition(0,R.anim.slide_out_right);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                long deltaTime=System.currentTimeMillis()-nowTime;
                Log.d("TimeInfo","deltaTime="+deltaTime);
                if (deltaTime>1300){
                    smslistView.setLongClickable(true);
                    Log.d("TimeInfo","执行长按");
                }else {
                    smslistView.setClickable(true);
                    Log.d("TimeInfo","执行点击");
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                smslistView.setClickable(true);
                smslistView.setLongClickable(true);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /*
     *先不用下拉刷新
     *因为下拉刷新与所使用的overscroll库的效果会冲突
     */
    public void RefreshListview(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newListview();
                        swipeRefreshLayout.setRefreshing(false);//停止刷新
                    }
                },500);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MessageActivity.this,MainActivity.class);
        startActivity(intent);
        MessageActivity.this.finish();
        overridePendingTransition(0,R.anim.down_from_top);
    }


    /*
     *上移删除动画
     */
    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                }
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al!=null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(300);
        v.startAnimation(anim);
    }
}
