package com.grean.dustguest.model;

import android.content.Context;

import com.grean.dustguest.SocketTask;
import com.grean.dustguest.protocol.GeneralClientProtocol;
import com.grean.dustguest.protocol.GeneralConfig;
import com.grean.dustguest.protocol.GeneralHistoryData;
import com.grean.dustguest.protocol.GeneralLogFormat;
import com.grean.dustguest.protocol.HistoryDataListener;
import com.grean.dustguest.protocol.LogListener;
import com.grean.dustguest.protocol.ProtocolLib;

/**
 * Created by weifeng on 2018/1/21.
 */

public class ScanDeviceState {
    private static ScanDeviceState instance = new ScanDeviceState();
    private GeneralConfig config = new GeneralConfig();
    private boolean run;
    private GeneralClientProtocol clientProtocol;
    private LocalServerListener listener;
    private ScanDeviceState() {

    }

    public GeneralConfig getConfig() {
        return config;
    }

    public static ScanDeviceState getInstance() {
        return instance;
    }

    public void setLocalServerListener (LocalServerListener listener){
        this.listener = listener;
    }

    /**
     * 启动扫描
     *
     */
    synchronized public void startScan(Context context,String id){
        config.setDevicesId(id);
        clientProtocol = ProtocolLib.getInstance().getClientProtocol();
        SocketTask.getInstance().startSocketHeart("192.168.1.100",8888,context, clientProtocol);
        if(!run) {
            new ScanRealTimeData().start();
        }

    }

    public boolean isConnect(){
        return run;
    }

    public void getHistoryData(long startDate, long endDate, HistoryDataListener historyDataListener, GeneralHistoryData historyData){
        clientProtocol.sendLastData(startDate,endDate,historyDataListener,historyData);
    }

    public void getLog(long startDate, long endDate, LogListener logListener, GeneralLogFormat logFormat){
        clientProtocol.sendReadLog(startDate,endDate,logListener,logFormat);
    }

    /**
     * 停止查询实时数据，断开本地服务链接
     */
    public void stopRun() {
        if(run) {
            run = false;
            SocketTask.getInstance().stopSocketHeart();
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan(){
        run = false;
    }

    /**
     * 重启扫描
     */
    synchronized public void restartScan(){
        if(!run) {
            new ScanRealTimeData().start();
        }
    }

    private class ScanRealTimeData extends Thread{
        private int times;
        @Override
        public void run() {

            times = 0;
            while (!interrupted()) {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!clientProtocol.sendGetOperateInit(config)) {
                    times++;
                    if(times > 3){
                        run = false;
                        break;
                    }
                }else {

                    clientProtocol.sendScanCommand();
                    run = true;
                    break;
                }
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clientProtocol.sendLoadSetting(config);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clientProtocol.sendDustMeterInfo(config);
            while (run&&(!interrupted())){
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clientProtocol.sendScanCommand();
            }
            run = false;
            if(listener!=null) {
                listener.OnDisconnectServer();
            }
        }
    }


}
