package com.grean.dustguest.protocol;

import android.util.Log;

import com.grean.dustguest.SocketTask;
import com.grean.dustguest.presenter.RealTimeDataDisplay;
import com.grean.dustguest.presenter.SettingInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weifeng on 2018/1/23.
 */

public class ClientProtocol implements GeneralClientProtocol{
    private static final String tag = "ClientProtocol";
    private SocketTask socketTask;
    private RealTimeDataDisplay realTimeDataDisplay;
    public ClientProtocol(){
        socketTask = SocketTask.getInstance();
    }
    @Override
    public void handleReceiveData(String rec) {

        try {
            JSONObject jsonObject = new JSONObject(rec);
            String type = JSON.getProtocolType(jsonObject);
            if(type.equals("realTimeData")){
                //Log.d(tag,rec);
                RealTimeDataFormat dataFormat = JSON.getRealTimeData(jsonObject);
                if(realTimeDataDisplay!=null) {
                    realTimeDataDisplay.show(dataFormat);
                }
            }else if(type.equals("operateInit")){
                Log.d(tag,rec);
                String name = JSON.getDustName(jsonObject);
                if(realTimeDataDisplay!=null){
                    realTimeDataDisplay.showDustName(name);
                }
            }else{
                Log.d(tag,rec);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean sendScanCommand() {
        try {
            return socketTask.send(JSON.readRealTimeData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendGetOperateInit() {
        try {
            return socketTask.send(JSON.readOperateInit());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setRealTimeDisplay(RealTimeDataDisplay display) {
        realTimeDataDisplay = display;
    }

    @Override
    public void sendLoadSetting(SettingInfo info) {

    }
}
