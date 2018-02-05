package com.grean.dustguest.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grean.dustguest.DbTask;
import com.grean.dustguest.presenter.LogSearchListener;
import com.grean.dustguest.protocol.GeneralLogFormat;
import com.grean.dustguest.protocol.LogListener;
import com.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


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
    private Context context;
    @Override
    public void onReadLogComplete() {
        hasNewLog = true;
    }

    public SearchLog(LogSearchListener listener,Context context){
        this.listener = listener;
        this.context = context;
    }

    public void getLog(long start,long end){
        lastIndex = end;
        new GetLogThread(start,end,true).start();
    }

    public void saveLogToFile(String idString){
        if(context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")!= PackageManager.PERMISSION_GRANTED){
            listener.saveLogComplete(false,"请允许App写入外部存储器权限");
        }else {
            new SaveFileThread(logFormat, context,idString).start();
        }
    }

    private class SaveFileThread extends Thread{
        private GeneralLogFormat format;
        private Context con;
        private String fileName,pathName;
        private String id;

        public SaveFileThread(GeneralLogFormat logFormat,Context context,String id){
            this.format = logFormat;
            this.con = context;
            this.id = id;
        }

        @Override
        public void run() {
            super.run();
            DbTask dbTask = new DbTask(con,1);
            SQLiteDatabase db = dbTask.getWritableDatabase();

            if(hasLogBaseTable(id,db)){
                deleteTable(id,db);
            }
            saveLog2DataBase(id,db);

            db.close();
            dbTask.close();
            boolean success = exportLog2File(format);
            listener.saveLogComplete(success,pathName+fileName);
        }

        /**
         * 查询是否已经存在 该ID下的数据表
         * @param id
         * @param db
         * @return 有
         */
        private boolean hasLogBaseTable(String id,SQLiteDatabase db){
            String tableName = id+"_log";
            Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name",null);
            while (cursor.moveToNext()){
                Log.d(tag,cursor.getString(0));
                if(cursor.getString(0).equals(tableName)){
                    return true;
                }
            }
            return false;
        }

        /**
         * 如有存在表格，则删除
         * @param id
         * @param db
         */
        private void deleteTable(String id,SQLiteDatabase db){
            String tableName = id+"_log";
            db.execSQL("drop table "+tableName);

        }

        /**
         * 新建表，记录数据
         * @param id
         * @param db
         */
        private void saveLog2DataBase(String id,SQLiteDatabase db){
            String tableName = id+"_log";
            db.execSQL("CREATE TABLE "+tableName+" (num INTEGER PRIMARY KEY AUTOINCREMENT,content TEXT)");
            ContentValues values;
            List<String> list = format.getContent();
            for(int i=0;i<format.getSize();i++) {
                values = new ContentValues();
                values.put("content", list.get(i));
                db.insert(tableName, null, values);
            }
        }

        private boolean exportLog2File(GeneralLogFormat format) {
            boolean exportDataResult=true;
            pathName = "/storage/emulated/0/GREAN/";//"/mnt/user/0/GREAN/"; // /storage/sdcard0/GREAN/
            fileName = id+"日志"+ tools.nowTime2FileString()+"导出.txt";
            File path = new File(pathName);
            File file = new File(path,fileName);

            try{
                if (!path.exists()) {
                    //Log.d("TestFile", "Create the path:" + pathName);
                    path.mkdir();
                }
                if (!file.exists()) {
                    //Log.d("TestFile", "Create the file:" + fileName);
                    file.createNewFile();
                }

                // 导出日志
                BufferedWriter bw = new BufferedWriter(new FileWriter(file,false)); // true// 是添加在后面// false// 是每次写新的
                for (String tmp : format.getContent()) {
                    bw.write(tmp + "\r\n");
                    //Log.d("写入SD", tmp);
                }
                bw.flush();
                bw.close();


            }catch (IOException e) {
                e.printStackTrace();
                exportDataResult = false;
            }
            return exportDataResult;

        }

    }


    /**
     * 下拉刷新时，调用的方法
     * @param start
     * @param end 当前显示的结束时间
     * @return 下拉后
     */
    public long refreshLog(long start,long end){
        Log.d(tag,"start = "+tools.timestamp2string(start)+";end = "+tools.timestamp2string(end)+";index ="+tools.timestamp2string(lastIndex));
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
            lastIndex = endDate;
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
