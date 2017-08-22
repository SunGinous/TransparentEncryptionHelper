package com.example.administrator.myironguard.Service;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.administrator.myironguard.Bean.Sms;
import com.stericson.RootTools.RootTools;


public class DeleteMsgService extends IntentService {

    private String data;
    private Uri SMS_URI=Uri.parse("content://sms/inbox");
    private Sms sms=new Sms();
    public DeleteMsgService() {
        super("DeleteMsgService");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("TimeInfo","服务启动");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        data=intent.getStringExtra("number");
        //Log.d("TimeInfo","要删除短信的号码"+data);
        deleteMessage();
        Log.d("TimeInfo","执行删除");
    }

    public void deleteMessage(){

        String where="address='"+data+"'";
        /**
         * 注意有的手机号是+86开头的
         */
        String fullnumber="+86"+data;
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
