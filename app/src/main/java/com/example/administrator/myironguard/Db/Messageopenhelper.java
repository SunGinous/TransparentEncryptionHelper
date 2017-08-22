package com.example.administrator.myironguard.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunginous on 2017/2/8.
 */

public class Messageopenhelper extends SQLiteOpenHelper {
    public Messageopenhelper(Context context){
        super(context,"Message_Info",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table message(id varchar(20),name varchar(20),number varchar(20),content varchar(2000),date varchar(20),type integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
