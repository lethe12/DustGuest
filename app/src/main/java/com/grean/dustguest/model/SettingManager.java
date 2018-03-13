package com.grean.dustguest.model;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.grean.dustguest.DbTask;
import com.grean.dustguest.presenter.NotifyProcessDialogInfo;
import com.grean.dustguest.presenter.SettingDisplay;
import com.grean.dustguest.presenter.SettingManagerListener;
import com.grean.dustguest.protocol.GeneralClientProtocol;
import com.grean.dustguest.protocol.GeneralConfig;
import com.grean.dustguest.protocol.ProtocolLib;
import com.tools;

/**
 * Created by weifeng on 2018/1/26.
 */

public class SettingManager implements DustMeterCalCtrl{
    private static final String tag = "SettingManager";
    private SettingManagerListener listener;
    private GeneralClientProtocol protocol = ProtocolLib.getInstance().getClientProtocol();
    private GeneralConfig config = ScanDeviceState.getInstance().getConfig();
    private SettingDisplay info;
    private boolean dustMeterCalRun = false;
    private DustMeterCalThread thread;

    public SettingManager(SettingManagerListener listener){
        this.listener = listener;
    }

    public void loadSetting (){
        listener.showContent(config);
    }

    public void setDustParams(float k,float b){
        config.setDustParaK(k);
        config.setDustParaB(b);
        protocol.sendSetDustMeterParaK(k,b);
    }

    public void setAutoCalEnable(boolean key){
        config.setAutoCalEnable(key);
        protocol.sendUploadConfig(config);
    }

    public void setAutoDate(String dateString,String intervalString){
        long interval = Long.valueOf(intervalString)*3600000l;
        long date = Long.valueOf(tools.string2timestamp(dateString));
        config.setAutoCalInterval(interval);
        config.setAutoCalTime(date);
        protocol.sendUploadConfig(config);
    }

    public void setMotorParams(String timeString,String stepString){
        int time = Integer.valueOf(timeString);
        int step = Integer.valueOf(stepString);
        config.setMotorStep(step);
        config.setMotorTime(time);
        protocol.sendUploadConfig(config);
    }

    public void setAlarmValue(String alarm){
        config.setAlarmDust(Float.valueOf(alarm));
        protocol.sendUploadConfig(config);
    }

    public void setProtocol(String mnCode,int protocolName,String serverIp,int serverPort){
        config.setMnCode(mnCode);
        config.setClientProtocolName(protocolName);
        config.setServerIp(serverIp);
        config.setServerPort(serverPort);
        protocol.sendUploadConfig(config);
    }

    public void setDustName(int dustName){
        config.setDustName(dustName);
        protocol.sendUploadConfig(config);
    }

    public void updateSetting(SettingDisplay display){
        protocol.sendLoadSetting(config,display);
    }

    public String calcNextDate(String string,String intervalString){
        long plan = tools.string2timestamp(string);
        long now = tools.nowtime2timestamp();
        long interval = Long.valueOf(intervalString);
        long next;
        if (interval!=0){
            next = tools.calcNextTime(now,plan,interval);
        }else {
            next = now + 24*3600l;
        }
        return tools.timestamp2string(next);
    }

    public void startDownLoadSoftware(Context context, String url, NotifyProcessDialogInfo processDialogInfo, SettingDisplay operateInfo){
        this.info = operateInfo;
        new Thread(new DownloadRunnable(context,url,processDialogInfo,operateInfo)).start();
    }

    @Override
    public void onFinish() {
        dustMeterCalRun = false;

    }

    @Override
    public void onResult(String info) {
        this.info.cancelDialogBarStyleWithToast(info);
    }

    private class DownloadRunnable implements Runnable{

        private String url;
        private NotifyProcessDialogInfo info;
        private Context context;
        private SettingDisplay operateInfo;

        public DownloadRunnable(Context context, String url, NotifyProcessDialogInfo info, SettingDisplay operateInfo){
            this.context = context;
            this.url = url;
            this.info = info;
            this.operateInfo = operateInfo;
        }

        private void queryDownloadProcess(long requestId,DownloadManager downloadManager){
            DownloadManager.Query query= new DownloadManager.Query();
            query.setFilterById(requestId);
            try{
                boolean isGoing = true;
                int times = 0;
                while (isGoing){
                    Cursor cursor = downloadManager.query(query);
                    if(cursor!=null && cursor.moveToFirst()){
                        int state = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        switch (state){
                            case DownloadManager.STATUS_SUCCESSFUL:
                                isGoing = false;
                                operateInfo.cancelDialogWithToast("下载成功!");
                                break;
                            case DownloadManager.STATUS_FAILED:
                                isGoing = false;
                                operateInfo.cancelDialogWithToast("下载失败!");
                                break;
                            case DownloadManager.STATUS_PAUSED:
                                isGoing = false;
                                operateInfo.cancelDialogWithToast("下载失败!");
                                break;
                            case DownloadManager.STATUS_PENDING:
                                info.showInfo("准备下载");
                                break;
                            case DownloadManager.STATUS_RUNNING:
                                Log.d(tag,"下载中");
                                break;
                            default:
                                break;

                        }
                        Thread.sleep(200);
                        if(cursor!=null){
                            cursor.close();
                        }
                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d(tag,"下载完成");
        }

        private long startDownload(){
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long requestId = downloadManager.enqueue(CreateRequest(url));
            //myApplication.getInstance().getConfig().put("ID",requestId);
            queryDownloadProcess(requestId,downloadManager);
            return requestId;
        }



        private DownloadManager.Request CreateRequest(String url){

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            // Log.d(tag,url);
            // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ;
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,"123.apk");
            request.setDescription("杭州绿洁扬尘在线监测系统");
            return request;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            startDownload();
        }
    }

    private class DustMeterCalThread extends Thread{
        private NotifyProcessDialogInfo dialogInfo;
        private boolean zeroCal;

        public DustMeterCalThread(NotifyProcessDialogInfo dialogInfo,boolean zeroCal){
            this.dialogInfo = dialogInfo;
            dustMeterCalRun = true;
            this.zeroCal = zeroCal;
        }
        @Override
        public void run() {
            dialogInfo.showInfo("开始校准...0%");
            dialogInfo.showProcess(0);
            //ScanDeviceState.getInstance().stopScan();
            ScanDeviceState.getInstance().pauseScan();
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            protocol.sendDustMeterCalStart(dialogInfo);


            while (dustMeterCalRun&&(!interrupted())) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                protocol.sendDustMeterCalProcess(SettingManager.this);
            }

            protocol.sendDustMeterCalResult();
            Log.d(tag,"end DustMeterCal");
            //ScanDeviceState.getInstance().restartScan();
            ScanDeviceState.getInstance().resumeScan();
        }
    }

    public void startDustMeterCal(NotifyProcessDialogInfo dialogInfo,SettingDisplay display){
        this.info = display;
        thread = new DustMeterCalThread(dialogInfo,false);
        thread.start();
    }

    public void ctrlDustMeter(boolean key){
        protocol.sendCtrlDustMeter(key);
    }

    public void ctrlRelay(int num,boolean key,SettingDisplay info){
        this.info = info;
        new CtrlRelayThread().start();
        protocol.sendCtrlRelay(num,key);
    }

    public void ctrlMotorForwardTest(){
        protocol.sendCtrlMotorForwardTest();
    }

    public void ctrlMotorBackwardTest(){
        protocol.sendCtrlMotorBackwardTest();
    }

    public void ctrlMotorForwardStep(){
        protocol.sendCtrlMotorForwardStep();
    }

    public void ctrlMotorBackwardStep(){
        protocol.sendCtrlMotorBackwardStep();
    }

    private class CtrlRelayThread extends Thread{
        @Override
        public void run() {
            Log.d(tag,"CtrlRelayThread");
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(info!=null) {
                info.cancelDialog();
            }
        }
    }

    public void savePassword(Context context,String value){
        SystemConfig config = new SystemConfig(context);
        config.saveConfig("Password",value);
    }

    /**
     * 清理数据库
     * @param context
     */
    public void clearRecentDevices(Context context){
        DbTask dbTask = new DbTask(context,1);
        SQLiteDatabase db = dbTask.getWritableDatabase();
        db.execSQL("drop table devices");
        db.execSQL(DbTask.devicesSqlString);
        db.close();
    }

}
