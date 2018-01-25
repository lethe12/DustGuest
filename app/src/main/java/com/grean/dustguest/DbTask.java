package com.grean.dustguest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by weifeng on 2018/1/25.
 */

public class DbTask extends SQLiteOpenHelper{
    public DbTask(Context context, int version){
        super(context,"data.db",null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE devices (date LONG,id text ,dustName TEXT,config TEXT,dustMeter TEXT)");//最近更新的时间戳，设备编号，扬尘参数，设置参数，粉尘仪参数
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
