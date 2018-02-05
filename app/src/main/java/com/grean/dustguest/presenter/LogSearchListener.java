package com.grean.dustguest.presenter;

import java.util.List;

/**
 * Created by weifeng on 2018/2/2.
 */

public interface LogSearchListener {
    void showNewLog(List<String> log);
    void showRefreshLog(List<String> log,int index);
    void saveLogComplete(boolean success,String fileName);
}
