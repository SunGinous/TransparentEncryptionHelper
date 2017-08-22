package com.example.administrator.myironguard.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/12/24.
 */
public class Myopenhelper extends SQLiteOpenHelper{
    public Myopenhelper(Context context) {
        super(context, "Aes_info", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table myaes (key varchar(200),src varchar(100),sca varchar(100),des varchar(100),viewpath varchar(100),name varchar(50),date varchar(20),type integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
