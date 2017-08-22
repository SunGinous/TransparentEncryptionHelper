package com.example.administrator.myironguard.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunginous on 2017/2/25.
 * Messageopenhelper创建的数据库存储所有信息
 * 该Helper类创建的数据库仅存储联系人姓名和手机号，用于加载私密联系人界面
 */

public class Contractopenhelper extends SQLiteOpenHelper {
    public Contractopenhelper(Context context){
        super(context,"Contract_Info",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table contract(friendname varchar(20),friendnumber varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
