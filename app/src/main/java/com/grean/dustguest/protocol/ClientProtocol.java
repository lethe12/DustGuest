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
    private HistoryDataListener historyDataListener;
    private GeneralHistoryData historyData;
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
                String name = JSON.getDustName(jsonObject);
                if(realTimeDataDisplay!=null){
                    realTimeDataDisplay.showDustName(name);
                }
            }else if(type.equals("historyData")){
                //Log.d(tag,rec);
                if((historyDataListener!=null)&&(historyData!=null)){
                    JSON.getHistoryData(jsonObject,historyData);
                    historyDataListener.setHistoryData();
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
}
