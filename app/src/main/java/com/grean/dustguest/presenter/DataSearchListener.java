package com.grean.dustguest.presenter;

import java.util.List;

/**
 * Created by weifeng on 2018/1/31.
 */

public interface DataSearchListener {
    void searchData(long start,long end);
    void showAllData(List<String> date,List<List<String>> data);
    void saveDataComplete(boolean success,String fileName);
    void showDataDownLoadProcess(int process);
}
