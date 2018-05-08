package com.grean.dustguest.protocol;

import android.util.Log;

import com.grean.dustguest.SocketTask;
import com.grean.dustguest.model.DustMeterCalCtrl;
import com.grean.dustguest.model.DustMeterCalProcessFormat;
import com.grean.dustguest.presenter.NotifyProcessDialogInfo;
import com.grean.dustguest.presenter.RealTimeDataDisplay;
import com.grean.dustguest.presenter.RealTimeSettingDisplay;
import com.grean.dustguest.presenter.SettingDisplay;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weifeng on 2018/1/23.
 */

public class ClientProtocol implements GeneralClientProtocol{
    private static final String tag = "ClientProtocol";
    private SocketTask socketTask;
    private RealTimeDataDisplay realTimeDataDisplay;
    private RealTimeSettingDisplay realTimeSettingDisplay;
    private HistoryDataListener historyDataListener;
    private GeneralHistoryData historyData;
    private GeneralConfig config;
    private SettingDisplay settingDisplay;
    private NotifyProcessDialogInfo dialogInfo;
    private DustMeterCalCtrl dustMeterCalCtrl;
    private GeneralLogFormat logFormat;
    private LogListener logListener;
    private NoiseCalibrationStateListener noiseCalibrationStateListener;

    public ClientProtocol(){
        socketTask = SocketTask.getInstance();
    }
    @Override
    synchronized public void handleReceiveData(String rec) {

        try {
            JSONObject jsonObject = new JSONObject(rec);
            String type = JSON.getProtocolType(jsonObject);
            if(type.equals("realTimeData")){
                Log.d(tag,"实时"+rec);
                RealTimeDataFormat dataFormat = JSON.getRealTimeData(jsonObject);
                if(realTimeDataDisplay!=null) {
                    realTimeDataDisplay.show(dataFormat);
                }
                if(realTimeSettingDisplay!=null){
                    realTimeSettingDisplay.show(dataFormat);
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
            }else if(type.equals("historyHourData")){
                Log.d(tag,rec);
                if((historyDataListener!=null)&&(historyData!=null)){
                    JSON.getHistoryData(jsonObject,historyData);
                    historyDataListener.setHistoryData();
                }
            }else if(type.equals("log")){
                Log.d(tag,rec);
                if((logListener!=null)&&(logFormat!=null)){
                    JSON.getLog(jsonObject,logFormat);
                    Log.d(tag,"log size="+String.valueOf(logFormat.getSize()));
                    logListener.onReadLogComplete();
                }
            }else if(type.equals("downloadSetting")){
                if(config!=null){
                    JSON.getSetting(jsonObject,config);
                }
                if(settingDisplay!=null){
                    settingDisplay.show(config);
                }
            }else if(type.equals("operate")){
                if(jsonObject.has("DustMeterInfo")){
                    Log.d(tag,rec);
                    if(config!=null){
                        JSON.getDustMeterInfo(jsonObject,config);
                    }
                }

                if(jsonObject.has("NoiseCalibrationState")){
                    if(jsonObject.has("state")){
                        if(noiseCalibrationStateListener!=null){
                            noiseCalibrationStateListener.onState(jsonObject.getInt("state"));
                        }
                    }
                }

                if(jsonObject.has("DustMeterCalProcess")){
                    DustMeterCalProcessFormat format = JSON.getDustMeterCalProcess(jsonObject);
                    if(dialogInfo!=null){
                        dialogInfo.showInfo(format.getString());
                        dialogInfo.showProcess(format.getProcess());
                    }
                    if(format.getProcess() == 100) {
                        if (dustMeterCalCtrl != null) {
                            dustMeterCalCtrl.onFinish();
                        }
                    }
                }else if(jsonObject.has("DustMeterCalResult")){
                    String string =  JSON.getDustMeterCalResult(jsonObject);
                    if(dustMeterCalCtrl!=null){
                        dustMeterCalCtrl.onResult(string);
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
    public void sendSetDustMeterParaK(float k,float b) {
        try {
            socketTask.send(JSON.operateDustSetParaK(k,b));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendUploadConfig(GeneralConfig config) {
        try {
            socketTask.send(JSON.uploadConfig(config));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDustMeterCalStart(NotifyProcessDialogInfo dialogInfo) {
        this.dialogInfo = dialogInfo;
        try {
            socketTask.send(JSON.operateDustMeterCal());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDustMeterCalProcess(DustMeterCalCtrl ctrl) {
        this.dustMeterCalCtrl = ctrl;
        try {
            socketTask.send(JSON.operateDustMeterCalProcess());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendDustMeterCalResult() {
        try {
            socketTask.send(JSON.operateDustMeterCalResult());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCtrlDustMeter(boolean key) {
        try {
            socketTask.send(JSON.ctrlDustMeter(key));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCtrlRelay(int num, boolean key) {
        try {
            socketTask.send(JSON.ctrlRelay(num,key));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCtrlMotorForwardTest() {
        try {
            socketTask.send(JSON.ctrlMotorForwardTest());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCtrlMotorBackwardTest() {
        try {
            socketTask.send(JSON.ctrlMotorBackwardTest());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCtrlMotorForwardStep() {
        try {
            socketTask.send(JSON.ctrlMotorForwardStep());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCtrlMotorBackwardStep() {
        try {
            socketTask.send(JSON.ctrlMotorBackwardStep());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendReadLog(long startDate,long endDate,LogListener logListener,GeneralLogFormat logFormat) {
        this.logFormat = logFormat;
        this.logListener = logListener;
        try {
            socketTask.send(JSON.readLog(startDate,endDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNoiseCalibration() {
        try {
            socketTask.send(JSON.operateNoiseCalibration());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNoiseCalibrationState(NoiseCalibrationStateListener listener) {
        try {
            this.noiseCalibrationStateListener = listener;
            socketTask.send(JSON.getNoiseCalibrationState());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRealTimeDisplay(RealTimeDataDisplay display) {
        realTimeDataDisplay = display;
    }

    @Override
    public void setRealTimeSettingDisplay(RealTimeSettingDisplay display) {
        realTimeSettingDisplay = display;
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
    public void sendLastHourData(long startDate, long endDate, HistoryDataListener listener, GeneralHistoryData historyData) {
        try {
            this.historyData = historyData;
            this.historyDataListener = listener;
            socketTask.send(JSON.readHistoryHourData(startDate,endDate));
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
    public void sendLoadSetting(GeneralConfig config, SettingDisplay display) {
        try {
            this.config = config;
            this.settingDisplay = display;
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
