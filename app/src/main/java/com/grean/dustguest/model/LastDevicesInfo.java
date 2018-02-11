package com.grean.dustguest.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.grean.dustguest.DbTask;
import com.grean.dustguest.protocol.GeneralConfig;
import com.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by weifeng on 2018/1/21.
 */

public class LastDevicesInfo {
    private Context context;
    private static final String tag = "LastDevicesInfo";

    public LastDevicesInfo(Context context){
        this.context = context;
    }

    public String [] getLastDevicesList(){
        DbTask dbTask = new DbTask(context,1);
        SQLiteDatabase db = dbTask.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from devices",null);
        int size = cursor.getCount();
        if(size!=0){
            String [] ids = new String[size];
            for(int i=0;i<size;i++){
                if(cursor.moveToNext()){
                    ids[i] = cursor.getString(1);
                }
            }
            db.close();
            dbTask.close();
            return ids;
        }else{
            db.close();
            dbTask.close();
            return null;
        }
    }

    public void loadDeviceConfig(String dustName,String configContent,String dustMeterInfo){
        GeneralConfig config = ScanDeviceState.getInstance().getConfig();
        try {
            if(dustName!=null) {
                config.setDustNameContent(new JSONObject(dustName));
            }
            if(configContent!=null) {
                config.setConfigContent(new JSONObject(configContent));
            }
            if(dustMeterInfo!=null) {
                config.setDustMeterInfoContent(new JSONObject(dustMeterInfo));
            }
        } catch (JSONException e) {
            Log.d(tag,"解析出错");
            e.printStackTrace();
        }
    }

    public void saveConfig(){
        GeneralConfig config = ScanDeviceState.getInstance().getConfig();
        DbTask dbTask = new DbTask(context,1);
        SQLiteDatabase db = dbTask.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id from devices where id='"+config.getDevicesId()+"'",null);
        ContentValues values = new ContentValues();
        values.put("date", tools.nowtime2timestamp());
        values.put("dustName", config.getDustNameContent());
        values.put("config", config.getConfigContent());
        values.put("dustMeter", config.getDustMeterInfoContent());
        //Log.d(tag,"dustName:"+config.getDustNameContent()+";config:"+config.getConfigContent()+";dustMeterInfo:"+config.getDustMeterInfoContent());
        if(cursor.getCount()!=0){//有重复的
            Log.d(tag,"有重复"+String.valueOf(cursor.getCount()));
            db.update("devices",values,"id=?",new String[]{config.getDevicesId()});
        }else {
            Log.d(tag,"新建数据");
            values.put("id", config.getDevicesId());
            db.insert("devices", null, values);
        }
        db.close();
        dbTask.close();
    }

}
