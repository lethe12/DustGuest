package com.grean.dustguest.protocol;

import com.grean.dustguest.model.DustMeterCalCtrl;
import com.grean.dustguest.presenter.NotifyProcessDialogInfo;
import com.grean.dustguest.presenter.RealTimeDataDisplay;
import com.grean.dustguest.presenter.RealTimeSettingDisplay;
import com.grean.dustguest.presenter.SettingDisplay;

/**
 * Created by weifeng on 2018/1/23.
 */

public interface GeneralClientProtocol {
    void handleReceiveData(String rec);
   // void setShowRealTimeData(ShowRealTimeData showRealTimeData);
    boolean sendScanCommand();

    void setRealTimeDisplay(RealTimeDataDisplay display);
    void setRealTimeSettingDisplay(RealTimeSettingDisplay display);
    void sendLastData(long startDate,long endDate,HistoryDataListener listener,GeneralHistoryData historyData);
    void sendLoadSetting(GeneralConfig config);
    void sendLoadSetting(GeneralConfig config, SettingDisplay display);
    void sendDustMeterInfo(GeneralConfig config);
    boolean sendGetOperateInit(GeneralConfig config);
    void sendSetDustMeterParaK(float paraK,float paraB);
    void sendUploadConfig(GeneralConfig config);
    void sendDustMeterCalStart(NotifyProcessDialogInfo dialogInfo);
    void sendDustMeterCalProcess(DustMeterCalCtrl ctrl);
    void sendDustMeterCalResult();

    void sendCtrlDustMeter(boolean key);
    void sendCtrlRelay(int num,boolean key);
    void sendCtrlMotorForwardTest();
    void sendCtrlMotorBackwardTest();
    void sendCtrlMotorForwardStep();
    void sendCtrlMotorBackwardStep();
    void sendReadLog(long startDate,long endDate,LogListener logListener,GeneralLogFormat logFormat);

    void sendNoiseCalibration();
    void sendNoiseCalibrationState(NoiseCalibrationStateListener listener);

    /*void sendCalDust(ShowOperateInfo info,float target);
    void sendSetDustMeterParaK(float parameter);


    void sendDustMeterCalZeroStart(NotifyProcessDialogInfo dialogInfo);


    void sendDustMeterInfo(ShowOperateInfo info);
    void sendExportData(NotifyDataInfo info,long start,long end);
    void sendExportDataInfo(NotifyDataInfo info, InquireExportDataProcess exportDataProcess);
    void sendHistoryData(NotifyDataInfo info,long date);*/
}
