package com.grean.dustguest.model;

import android.content.Context;

import com.grean.dustguest.SocketTask;
import com.grean.dustguest.presenter.SettingInfo;
import com.grean.dustguest.protocol.GeneralClientProtocol;
import com.grean.dustguest.protocol.ProtocolLib;

/**
 * Created by weifeng on 2018/1/21.
 */

public class ScanDeviceState implements SettingInfo{
    private static ScanDeviceState instance = new ScanDeviceState();
    private boolean run;
    private GeneralClientProtocol clientProtocol;
    private ScanDeviceState() {

    }

    public static ScanDeviceState getInstance() {
        return instance;
    }

    /**
     * 启动扫描
     *
     */
    public void startScan(Context context){
        clientProtocol = ProtocolLib.getInstance().getClientProtocol();
        SocketTask.getInstance().startSocketHeart("192.168.1.100",8888,context, clientProtocol);
        new ScanRealTimeData().start();

    }

    public boolean isConnect(){
        return run;
    }


    /**
     * 停止查询实时数据，断开本地服务链接
     */
    public void stopRun() {
        run =false;
        SocketTask.getInstance().stopSocketHeart();
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
                if (!clientProtocol.sendGetOperateInit()) {
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
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clientProtocol.sendLoadSetting(instance);
            while (run&&(!interrupted())){
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clientProtocol.sendScanCommand();
            }
            SocketTask.getInstance().stopSocketHeart();
            run = false;
        }
    }
}