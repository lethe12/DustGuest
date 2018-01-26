package com.grean.dustguest.model;

import com.grean.dustguest.presenter.SettingManagerListener;

/**
 * Created by weifeng on 2018/1/26.
 */

public class SettingManager {
    private SettingManagerListener listener;

    public SettingManager(SettingManagerListener listener){
        this.listener = listener;
    }

    public void loadSetting (){
        listener.showContent(ScanDeviceState.getInstance().getConfig());
    }


}
