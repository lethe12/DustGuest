package com.grean.dustguest.presenter;

import com.grean.dustguest.protocol.RealTimeDataFormat;

/**
 * Created by weifeng on 2018/1/23.
 */

public interface RealTimeDataDisplay {
    void show(RealTimeDataFormat format);
    void showDustName(String name);
    void showLastData(String[] date,float [] data);
}
