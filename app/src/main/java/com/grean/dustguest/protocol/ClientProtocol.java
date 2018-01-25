package com.grean.dustguest.protocol;

import android.util.Log;

import com.grean.dustguest.SocketTask;
import com.grean.dustguest.presenter.RealTimeDataDisplay;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weifeng on 2018/1/23.
 */

public class ClientProtocol implements GeneralClientProtocol{
    private static final String tag = "ClientProtocol";
    private SocketTask socketTask;
    private RealTimeDataDisplay realTimeDataDisplay;
    private HistoryDataListener historyDataListener;
    private GeneralHistoryData historyData;
    private GeneralConfig config;
    public ClientProtocol(){
        socketTask = SocketTask.getInstance();
    }
    @Override
    synchronized public void handleReceiveData(String rec) {

        try {
            JSONObject jsonObject = new JSONObject(rec);
            String type = JSON.getProtocolType(jsonObject);
            if(type.equals("realTimeData")){
               // Log.d(tag,"实时");
                RealTimeDataFormat dataFormat = JSON.getRealTimeData(jsonObject);
                if(realTimeDataDisplay!=null) {
                    realTimeDataDisplay.show(dataFormat);
                }
            }else if(type.equals("operateInit")){
                if((realTimeDataDisplay!=null)&&(config!=null)){
                    JSON.getDustName(jsonObject,config);
                    String name = config.getDustNames()[config.getDustName()];
                    realTimeDataDisplay.showDustName(name);
                }
            }else if(type.equals("historyData")){
                //Log.d(tag,rec);
                if((historyDataListener!=null)&&(historyData!=null)){
                    JSON.getHistoryData(jsonObject,historyData);
                    historyDataListener.setHistoryData();
                }
            }else if(type.equals("downloadSetting")){
                if(config!=null){
                    JSON.getSetting(jsonObject,config);
                }
            }else if(type.equals("operate")){
                if(jsonObject.has("DustMeterInfo")){
                    if(config!=null){
                        JSON.getDustMeterInfo(jsonObject,config);
                    }
                }
            }else{
                Log.d(tag,"协议异常"+rec);
            }
        } catch (JSONException e) {
            Log.d(tag,"json异常"+rec);
            e.printStackTrace();
        }
    }



    @Override
    synchronized public boolean sendScanCommand() {
        try {
            return socketTask.send(JSON.readRealTimeData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendGetOperateInit(GeneralConfig config) {
        try {
            this.config = config;
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
    public void sendLastData(long startDate, long endDate, HistoryDataListener listener,GeneralHistoryData historyData) {
        try {
            this.historyData = historyData;
            this.historyDataListener = listener;
            socketTask.send(JSON.readHistoryData(startDate,endDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLoadSetting(GeneralConfig config) {
        try {
            this.config = config;
            socketTask.send(JSON.readSetting());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDustMeterInfo(GeneralConfig config) {
        try {
            this.config = config;
            socketTask.send(JSON.readDustMeterInfo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
