package com.grean.dustguest.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grean.dustguest.DbTask;
import com.grean.dustguest.presenter.DialogProcessFragmentBarStyle;
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
    private String deviceId,dustName,config,dustMeterInfo;
    private long lastConnectDate;

    public LocalServerManager(Context context,LocalServerListener listener){
        this.listener = listener;
        this.context = context;
    }

    public boolean isConnect() {
        return connect;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDustName() {
        return dustName;
    }

    public String getConfig() {
        return config;
    }

    public String getDustMeterInfo() {
        return dustMeterInfo;
    }

    public long getLastConnectDate() {
        return lastConnectDate;
    }

    public void stopScan(){
        ScanDeviceState.getInstance().stopScan();
    }

    /**
     * 本地是否拥有该ID的信息，
     * @return
     */
    public boolean hasDevicesInfo(String id){
        boolean hasSameDevice = false;
        DbTask dbTask = new DbTask(context,1);
        SQLiteDatabase db = dbTask.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from devices",null);
        while (cursor.moveToNext()){
            if(id.equals(cursor.getString(1))){//如果已经连接过的设备
                Log.d(tag,"已连接设备");
                lastConnectDate = cursor.getLong(0);
                dustName = cursor.getString(2);
                config = cursor.getString(3);
                dustMeterInfo = cursor.getString(4);
                hasSameDevice = true;
                Log.d(tag,"dustName:"+dustName+";config:"+config+";dustMeterInfo:"+dustMeterInfo);
                break;
            }
        }
        db.close();
        dbTask.close();
        return hasSameDevice;
    }


    /**
     * 启动本地服务
     * @param id 目标设备ID
     */
    public void startLocalServer(String id,DialogProcessFragmentBarStyle dialog){
        deviceId = id;
        connect = false;
        if(wifiAdmin==null){
            wifiAdmin = new WifiAdmin(context);
        }
        wifiAdmin.openWifi();

        //wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("GreanDust","1234567890",3));
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("greanYc"+id,"1234567890",3));
        new StartLocalServer(ScanDeviceState.getInstance(),dialog).start();
    }

    private class StartLocalServer extends Thread{
        private ScanDeviceState state;
        private DialogProcessFragmentBarStyle dialog;

        public StartLocalServer(ScanDeviceState state,DialogProcessFragmentBarStyle dialog){
            this.state = state;
            this.dialog = dialog;
            state.setLocalServerListener(listener);
        }

        @Override
        public void run() {
            if(state.isConnect()){
                //Log.d(tag,"已经链接，准备重连");
                dialog.showInfo("已连接设备，正在断开");
                dialog.showProcess(2);
                state.stopRun();
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dialog.showInfo("正在新建连接");
            dialog.showProcess(15);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dialog.showProcess(25);
            state.startScan(context,deviceId);
            for (int i=0;i<10;i++){
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.showProcess(25+i*4);
                if(state.isConnect()){
                    break;
                }
            }
            dialog.showProcess(70);
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dialog.showProcess(100);
            String [] wifiInfo = wifiAdmin.getWifiInfo().split(",");
            Log.d(tag,"id="+deviceId+";ssid="+wifiInfo[0]);
            if((wifiInfo[0].equals("SSID: greanYc"+deviceId))&&state.isConnect()) {
                Log.d(tag,"已连接");
                connect = true;
                state.resumeScan();
            }else{
                if(state.isConnect()) {
                    Log.d(tag,"停扫描");
                    state.stopScan();
                }
                Log.d(tag,"未连接");
                connect = false;
            }
            listener.OnLocalServerResult(connect);
        }
    }

}
