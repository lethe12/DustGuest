package com.grean.dustguest.model;

import android.util.Log;

import com.grean.dustguest.presenter.DataSearchListener;
import com.grean.dustguest.protocol.GeneralClientProtocol;
import com.grean.dustguest.protocol.GeneralHistoryData;
import com.grean.dustguest.protocol.HistoryDataListener;
import com.grean.dustguest.protocol.ProtocolLib;

/**
 * Created by weifeng on 2018/1/31.
 */

public class SearchData implements HistoryDataListener {
    private static final String tag = "SearchData";
    private DataSearchListener dataSearchListener;
    private GeneralHistoryData historyData = new GeneralHistoryData();
    private long startDate,endDate;//,indexDate;
    private ScanDeviceState state = ScanDeviceState.getInstance();
    private boolean hasNewData;
    public SearchData(DataSearchListener listener){
        this.dataSearchListener = listener;
    }

    @Override
    public void setHistoryData() {
        Log.d(tag,"收到新的历史数据");
        hasNewData = true;
    }

    public void readyToSearchData(long start,long end){
        if(start < end){
            startDate = start;
            endDate = end;
        }else if(start > end){
            startDate = end;
            endDate = start;
        }else{
            if(dataSearchListener!=null) {//显示历史数据
                dataSearchListener.showAllData(null, null);
            }
            return;
        }
        new GetHistoryDataThread(startDate,endDate,this).start();

    }

    private class GetHistoryDataThread extends Thread{
        private long start,end,index;
        private HistoryDataListener listener;
        private static final long SearchInterval = 900000l;

        public GetHistoryDataThread(long start,long end,HistoryDataListener listener){
            this.start = start;
            this.end = end;
            index = startDate;
            this.listener = listener;
        }
        @Override
        public void run() {
            //每次搜索0.25小时数据,搜索完成后显示结果
            long next = index + SearchInterval;
            int times = 0;
            historyData.clear();
            while (next <= endDate) {
                hasNewData = false;
                state.getHistoryData(index, next, listener, historyData);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(hasNewData){
                    times = 0;
                    index = next;
                    next = index+SearchInterval;
                }else{
                    times++;
                }

                if(times > 5){
                    Log.d(tag,"查询历史数据超时");
                    break;
                }
            }

            if(hasNewData){
                if(index < endDate){
                    state.getHistoryData(index, endDate, listener, historyData);
                }
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(dataSearchListener!=null) {//显示历史数据
                    dataSearchListener.showAllData(historyData.getDate(), historyData.getData());
                }
            }


        }
    }
}
