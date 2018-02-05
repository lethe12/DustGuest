package com.grean.dustguest.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 * Created by weifeng on 2018/2/5.
 */

public class SystemConfig {
    private Context context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public SystemConfig(Context context){
        this.context = context;
        sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void saveConfig(String key,String value){
        editor.putString(key,value);
        editor.commit();
    }

    public String getString(String key){
        return sp.getString(key,null);
    }
}
