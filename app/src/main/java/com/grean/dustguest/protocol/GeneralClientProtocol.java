package com.grean.dustguest.protocol;

import com.grean.dustguest.presenter.RealTimeDataDisplay;

/**
 * Created by weifeng on 2018/1/23.
 */

public interface GeneralClientProtocol {
    void handleReceiveData(String rec);
   // void setShowRealTimeData(ShowRealTimeData showRealTimeData);
    boolean sendScanCommand();

    void setRealTimeDisplay(RealTimeDataDisplay display);
    void sendLastData(long startDate,long endDate,HistoryDataListener listener,GeneralHistoryData historyData);
    void sendLoadSetting(GeneralConfig config);
    void sendDustMeterInfo(GeneralConfig config);
    boolean sendGetOperateInit(GeneralConfig config);
    /*void sendCalDust(ShowOperateInfo info,float target);
    void sendSetDustMeterParaK(float parameter);
    void sendUploadSetting(SettingFormat format);
    void sendDustMeterCalStart(NotifyProcessDialogInfo dialogInfo);
    void sendDustMeterCalZeroStart(NotifyProcessDialogInfo dialogInfo);
    void sendDustMeterCalResult();
    void sendDustMeterCalProcess(DustMeterCalCtrl ctrl);
    void sendDustMeterInfo(ShowOperateInfo info);
    void sendExportData(NotifyDataInfo info,long start,long end);
    void sendExportDataInfo(NotifyDataInfo info, InquireExportDataProcess exportDataProcess);
    void sendHistoryData(NotifyDataInfo info,long date);*/
}
