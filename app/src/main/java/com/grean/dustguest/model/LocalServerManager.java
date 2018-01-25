package com.grean.dustguest.model;

import android.content.Context;
import android.util.Log;

import com.grean.dustguest.protocol.HistoryDataListener;
import com.wifi.WifiAdmin;

import static android.R.attr.tag;

/**
 * 处理本地服务
 * Created by weifeng on 2018/1/22.
 */

public class LocalServerManager {
    private static final String tag = "LocalServerManager";
    private Context context;
    private LocalServerListener listener;
    private WifiAdmin wifiAdmin;
    private boolean connect = false;
    private String deviceId;

    public LocalServerManager(Context context,LocalServerListener listener){
        this.listener = listener;
        this.context = context;
    }

    public boolean isConnect() {
        return connect;
    }



    /**
     * 启动本地服务
     * @param id 目标设备ID
     */
    public void startLocalServer(String id){
        deviceId = id;
        connect = false;
        if(wifiAdmin==null){
            wifiAdmin = new WifiAdmin(context);
        }
        wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("GreanDust","1234567890",3));
        new StartLocalServer(ScanDeviceState.getInstance()).start();
    }

    private class StartLocalServer extends Thread{
        private ScanDeviceState state;

        public StartLocalServer(ScanDeviceState state){
            this.state = state;
        }

        @Override
        public void run() {
            if(state.isConnect()){
                Log.d(tag,"已经链接，准备重连");
                state.stopRun();
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            state.startScan(context,deviceId);
            for (int i=0;i<10;i++){
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(state.isConnect()){
                    break;
                }
            }
            try {
                sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connect = state.isConnect();
            listener.OnLocalServerResult(connect);
        }
    }

}
