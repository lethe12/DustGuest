package com.grean.dustguest.model;

/**
 * Created by weifeng on 2018/1/30.
 */

public interface DustMeterCalCtrl {
    /**
     * 结束校准
     */
    void onFinish();

    /**
     * 显示校准结果
     * @param info
     */
    void onResult(String info);
}
