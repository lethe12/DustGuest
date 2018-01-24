package com.grean.dustguest.model;

import com.grean.dustguest.presenter.RealTimeDataDisplay;
import com.grean.dustguest.protocol.GeneralHistoryData;
import com.grean.dustguest.protocol.GeneralMinData;
import com.grean.dustguest.protocol.HistoryDataListener;
import com.tools;

/**
 * 查询最近15min数据
 * Created by weifeng on 2018/1/24.
 */

public class LastDataInfo implements HistoryDataListener{
    private RealTimeDataDisplay display;
    private int name;
    public static final int Dust=0,Temperate=1,Humidity=2,Pressure=3,WindForce=4,
            WindDirection=5,Noise=6,Value=7;

    public LastDataInfo(RealTimeDataDisplay display){
        this.display = display;

    }

    public void getLastMinData(int name){
        this.name = name;
        long now = tools.nowtime2timestamp();
        ScanDeviceState.getInstance().getHistoryData(now-15*60000,now,this);
    }

    @Override
    public void setHistoryData(GeneralHistoryData historyData) {
        int size = historyData.getSize();
        if(size > 1) {
            String [] date = new String[size];
            float [] data = new float[size];
            for (int i=0;i<size;i++){
                GeneralMinData minData = historyData.get(i);
                date[size-i-1] = tools.timestamp2StringHHMM(minData.getDate());
                data[size-i-1] = getValue(minData);
            }
            display.showLastData(date,data);
        }
    }

    private float getValue(GeneralMinData data){
        switch (name){
            case Dust:
                return data.getDust();
            case Temperate:
                return data.getTemperate();
            case Humidity:
                return data.getHumidity();
            case Pressure:
                return data.getPressure();
            case WindForce:
                return data.getWindForce();
            case WindDirection:
                return data.getWindDirection();
            case Noise:
                return data.getNoise();
            case Value:
                return data.getValue();
            default:
                return 0;

        }
    }
}
