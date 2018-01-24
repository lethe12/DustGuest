package com.grean.dustguest.protocol;

import com.grean.dustguest.presenter.RealTimeDataDisplay;
import com.grean.dustguest.presenter.SettingInfo;

/**
 * Created by weifeng on 2018/1/23.
 */

public interface GeneralClientProtocol {
    void handleReceiveData(String rec);
   // void setShowRealTimeData(ShowRealTimeData showRealTimeData);
    boolean sendScanCommand();
    boolean sendGetOperateInit();
    void setRealTimeDisplay(RealTimeDataDisplay display);
    void sendLoadSetting(SettingInfo info);
    void sendLastData(long startDate,long endDate,HistoryDataListener listener);
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
