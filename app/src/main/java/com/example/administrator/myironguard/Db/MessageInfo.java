package com.example.administrator.myironguard.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.myironguard.Bean.Sms;

import java.util.ArrayList;


public class MessageInfo {
    private Messageopenhelper messageopenhelper;
    public MessageInfo(Context context){
        messageopenhelper=new Messageopenhelper(context);
    }
    public long save(Sms sms){
        long i = -1;
        SQLiteDatabase db=messageopenhelper.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",sms.id);
        contentValues.put("name",sms.name);
        contentValues.put("number",sms.number);
        contentValues.put("content",sms.content);
        contentValues.put("date",sms.date);
        contentValues.put("type",sms.type);
        i=db.insert("message",null,contentValues);
        db.close();
        return i;
    }

    public ArrayList<Sms> query(){
        ArrayList<Sms> arrayList=new ArrayList<Sms>();
        SQLiteDatabase db=messageopenhelper.getReadableDatabase();
        String sql = "select * from message";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor!=null&&cursor.getCount()>0){
            while (cursor.moveToNext()){
                Sms sms=new Sms();
                sms.id=cursor.getString(0);
                sms.name=cursor.getString(1);
                sms.number=cursor.getString(2);
                sms.content=cursor.getString(3);
                sms.date=cursor.getString(4);
                sms.type=cursor.getInt(5);
                arrayList.add(sms);
            }
        }
        db.close();
        cursor.close();
        return arrayList;
    }

    public void delete(Sms sms){
        SQLiteDatabase db=messageopenhelper.getReadableDatabase();
        db.delete("message","number=?",new String[]{sms.number});
        db.close();
    }

    public boolean checkExist(String num){
        boolean result = false;
        Cursor cursor=null;
        SQLiteDatabase db =messageopenhelper.getReadableDatabase();
        String sql="select count(*) from message where number='"+num.trim()+"'";
        cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            if(cursor.getInt(0)!=0){ //cursor.getInt(),如果数据未null,返回0
                result=true;
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<Sms> queryByNumber(String number){
        ArrayList<Sms> arrayList=new ArrayList<Sms>();
        SQLiteDatabase db=messageopenhelper.getReadableDatabase();
        String sql = "select * from message where number='"+number.trim()+"'";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor!=null&&cursor.getCount()>0){
            while (cursor.moveToNext()){
                Sms sms=new Sms();
                sms.id=cursor.getString(0);
                sms.name=cursor.getString(1);
                sms.number=cursor.getString(2);
                sms.content=cursor.getString(3);
                sms.date=cursor.getString(4);
                sms.type=cursor.getInt(5);
                arrayList.add(sms);
            }
        }
        db.close();
        cursor.close();
        return arrayList;
    }
}
