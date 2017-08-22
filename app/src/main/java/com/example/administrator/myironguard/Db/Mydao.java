package com.example.administrator.myironguard.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.myironguard.Bean.Aes_bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/24.
 */
public class Mydao {
    private Myopenhelper mMyopenhelper;
    public Mydao(Context context)
    {
        mMyopenhelper= new Myopenhelper(context);
    }

    public long save(Aes_bean aes_bean)
    {   long i=-1;
        SQLiteDatabase db =mMyopenhelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("key",aes_bean.key);
        contentValues.put("src",aes_bean.src);
        contentValues.put("sca",aes_bean.sca);
        contentValues.put("des",aes_bean.des);
        contentValues.put("viewpath",aes_bean.viewpath);
        contentValues.put("name",aes_bean.name);
        contentValues.put("date",aes_bean.date);
        contentValues.put("type",aes_bean.type);
        i=db.insert("myaes",null,contentValues);

        db.close();
        return i;

    }

    public  ArrayList<Aes_bean> query()
    {
        ArrayList<Aes_bean>  arraylist = new ArrayList<Aes_bean>();
        SQLiteDatabase db =mMyopenhelper.getReadableDatabase();
        String sql = "select * from myaes";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor!=null && cursor.getCount()>0)
        {
            while(cursor.moveToNext())
            {
                Aes_bean aesbean = new Aes_bean();
                aesbean.key=cursor.getString(0);
                aesbean.src=cursor.getString(1);
                aesbean.sca=cursor.getString(2);
                aesbean.des=cursor.getString(3);
                aesbean.viewpath=cursor.getString(4);
                aesbean.name=cursor.getString(5);
                aesbean.date=cursor.getString(6);
                aesbean.type=cursor.getInt(7);
                arraylist.add(aesbean);
            }
        }

        db.close();
        cursor.close();
        return  arraylist;
    }
    public void delete (Aes_bean aes_bean)
    {
        SQLiteDatabase db =mMyopenhelper.getReadableDatabase();
        db.delete("myaes","name=?",new String[]{aes_bean.name});
        db.close();
    }
    public long checkNum(){
        SQLiteDatabase db=mMyopenhelper.getReadableDatabase();
        String sql="select count(*) from myaes";
        Cursor cursor=db.rawQuery(sql,null);
        cursor.moveToNext();
        long count=cursor.getLong(0);
        cursor.close();
        return count;
    }
}
