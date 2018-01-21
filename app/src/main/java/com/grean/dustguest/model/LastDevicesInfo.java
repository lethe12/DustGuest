package com.grean.dustguest.model;

import android.content.Context;

/**
 * Created by weifeng on 2018/1/21.
 */

public class LastDevicesInfo {
    private Context context;

    public LastDevicesInfo(Context context){
        this.context = context;

    }

    public String [] getLastDevicesList(){

        return new String[]{"123","345","789"};
    }
}
