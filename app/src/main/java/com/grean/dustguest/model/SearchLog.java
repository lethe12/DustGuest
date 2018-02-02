package com.grean.dustguest.model;

import android.util.Log;

import com.grean.dustguest.presenter.LogSearchListener;
import com.grean.dustguest.protocol.GeneralLogFormat;
import com.grean.dustguest.protocol.LogListener;
import com.tools;

/**
 * Created by weifeng on 2018/2/2.
 */

public class SearchLog implements LogListener{
    private static final String tag = "SearchLog";
    private static final long SearchInterval = 3600000l*6;
    private LogSearchListener listener;
    private long lastIndex = 0;
    private ScanDeviceState state = ScanDeviceState.getInstance();
    private GeneralLogFormat logFormat = new GeneralLogFormat();
    private boolean hasNewLog;
    @Override
    public void onReadLogComplete() {
        hasNewLog = true;
    }

    public SearchLog(LogSearchListener listener){
        this.listener = listener;
    }

    public void getLog(long start,long end){
        lastIndex = end;
        new GetLogThread(start,end,true).start();
    }

    /**
     * 下拉刷新时，调用的方法
     * @param start
     * @param end 当前显示的结束时间
     * @return 下拉后
     */
    public long refreshLog(long start,long end){
        if(lastIndex == 0){//没有查询过，直接刷新，显示
            new GetLogThread(start,end,true).start();
            lastIndex = end;
        }else{
            long now = tools.nowtime2timestamp(),endDate;
            if(now < (lastIndex + SearchInterval)){
                endDate = now;
            }else{
                endDate = lastIndex + SearchInterval;
            }
            new GetLogThread(lastIndex,endDate,false).start();
            lastIndex = end;
        }
        return lastIndex;
    }

    private class GetLogThread extends Thread{
        private long start,end,index;
        private boolean isNew;
        private int logIndex;
        public GetLogThread(long start,long end,boolean isNew){
            this.start = start;
            this.end = end;
            index = end-SearchInterval;
            this.isNew = isNew;
            if(isNew) {//新的一次搜索
                logFormat.clear();
            }else{
                logIndex = logFormat.getSize();
            }
        }

        @Override
        public void run() {
            int times = 0;
            while (index >= start){
                hasNewLog = false;
                state.getLog(index,end,SearchLog.this,logFormat);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(hasNewLog){
                    times = 0;
                    end = index;
                    index = end-SearchInterval;
                }else{
                    times++;
                }

                if(times > 5){
                    Log.d(tag,"查询历史数据超时");
                    break;
                }
            }

            if(hasNewLog) {
                if(start < end){
                    state.getLog(start,end,SearchLog.this,logFormat);
                }

                if (isNew) {
                    listener.showNewLog(logFormat.getContent());
                } else {
                    listener.showRefreshLog(logFormat.getContent(),logIndex);
                }
            }
        }
    }
}
