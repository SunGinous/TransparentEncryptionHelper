package com.example.administrator.myironguard.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.myironguard.Bean.Sms;

import java.util.ArrayList;


public class ContractInfo {
    private Contractopenhelper contractopenhelper;
    public ContractInfo(Context context){
        contractopenhelper=new Contractopenhelper(context);
    }
    public long save(Sms sms){
        long i = -1;
        SQLiteDatabase db=contractopenhelper.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("friendname",sms.name);
        contentValues.put("friendnumber",sms.number);
        i=db.insert("contract",null,contentValues);
        db.close();
        return i;
    }
    public ArrayList<Sms> query(){
        ArrayList<Sms> arrayList=new ArrayList<Sms>();
        SQLiteDatabase db=contractopenhelper.getReadableDatabase();
        String sql = "select * from contract";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor!=null&&cursor.getCount()>0){
            while (cursor.moveToNext()){
                Sms sms=new Sms();
                sms.name=cursor.getString(0);
                sms.number=cursor.getString(1);
                arrayList.add(sms);
            }
        }
        db.close();
        cursor.close();
        return arrayList;
    }
    public void delete(Sms sms){
        SQLiteDatabase db=contractopenhelper.getReadableDatabase();
        db.delete("contract","friendnumber=?",new String[]{sms.number});
        db.close();
    }
    public long checkNum(){
        SQLiteDatabase db=contractopenhelper.getReadableDatabase();
        String sql="select count(*) from contract";
        Cursor cursor=db.rawQuery(sql,null);
        cursor.moveToNext();
        long count=cursor.getLong(0);
        cursor.close();
        return count;
    }
}
