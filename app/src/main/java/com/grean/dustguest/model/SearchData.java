package com.grean.dustguest.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grean.dustguest.DbTask;
import com.grean.dustguest.presenter.DataSearchListener;
import com.grean.dustguest.protocol.GeneralClientProtocol;
import com.grean.dustguest.protocol.GeneralHistoryData;
import com.grean.dustguest.protocol.GeneralMinData;
import com.grean.dustguest.protocol.HistoryDataListener;
import com.grean.dustguest.protocol.ProtocolLib;
import com.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by weifeng on 2018/1/31.
 */

public class SearchData implements HistoryDataListener {
    private static final String tag = "SearchData";
    private static final String[] dataTitle={"分钟数据","小时数据"};
    private DataSearchListener dataSearchListener;
    private GeneralHistoryData historyData = new GeneralHistoryData();
    private long startDate,endDate;//,indexDate;
    private ScanDeviceState state = ScanDeviceState.getInstance();
    private boolean hasNewData;
    private int dataTileName=0;
    private Context context;
    public SearchData(DataSearchListener listener, Context context){
        this.dataSearchListener = listener;
        this.context = context;
    }

    public int getDataTileName() {
        return dataTileName;
    }

    public String[] getDataTitleStrings(){
        return dataTitle;
    }

    public void saveDataToFiles(String idString){
        if(context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")!= PackageManager.PERMISSION_GRANTED){
            dataSearchListener.saveDataComplete(false,"请允许App写入外部存储器权限");
        }else {
            new SaveFileThread(historyData, context,idString).start();
        }
    }

    private class SaveFileThread extends Thread{
        private GeneralHistoryData data;
        private Context con;
        private String fileName,pathName;
        private String id;

        public SaveFileThread(GeneralHistoryData data,Context context,String id){
            this.data = data;
            this.con = context;
            this.id = id;
        }

        @Override
        public void run() {
            super.run();
            DbTask dbTask = new DbTask(context,1);
            SQLiteDatabase db = dbTask.getWritableDatabase();

            if(hasDataBaseTable(id,db)){
                deleteTable(id,db);
            }
            saveData2DataBase(id,db);

            db.close();
            dbTask.close();
            boolean success = exportData2File(data);
            dataSearchListener.saveDataComplete(success,pathName+fileName);
        }

        /**
         * 查询是否已经存在 该ID下的数据表
         * @param id
         * @param db
         * @return 有
         */
        private boolean hasDataBaseTable(String id,SQLiteDatabase db){
            String tableName = "data_"+id;
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
            String tableName = "data_"+id;
            db.execSQL("drop table "+tableName);

        }

        /**
         * 新建表，记录数据
         * @param id
         * @param db
         */
        private void saveData2DataBase(String id,SQLiteDatabase db){
            String tableName = "data_"+id;
            db.execSQL("CREATE TABLE "+tableName+" (date LONG,dust FLOAT,value FLOAT,temperature FLOAT,humidity FLOAT,pressure FLOAT,windforce FLOAT,winddirection FLOAT,noise FLOAT)");
            ContentValues values;
            GeneralMinData minData;
            for(int i=0;i<data.getSize();i++) {
                minData = data.get(i);
                values = new ContentValues();
                values.put("date", minData.getDate());
                values.put("dust", minData.getDust());
                values.put("value", minData.getValue());
                values.put("temperature", minData.getTemperate());
                values.put("humidity", minData.getHumidity());
                values.put("pressure", minData.getPressure());
                values.put("windforce", minData.getWindForce());
                values.put("winddirection", minData.getWindDirection());
                values.put("noise", minData.getNoise());
                db.insert(tableName, null, values);
            }
        }

        private boolean exportData2File(GeneralHistoryData data) {
            boolean exportDataResult=true;
            pathName = "/storage/emulated/0/GREAN/";//"/mnt/user/0/GREAN/"; // /storage/sdcard0/GREAN/
            if(dataTileName == 1) {
                fileName = id + "小时数据" + tools.nowTime2FileString() + "导出.xls";
            }else{
                fileName = id + "分钟数据" + tools.nowTime2FileString() + "导出.xls";
            }
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

                WritableWorkbook wwb;
                OutputStream os = new FileOutputStream(file);
                wwb = Workbook.createWorkbook(os);

                //ArrayList<HistoryDataFormat> list = exportDataFormat(start,end);
                WritableSheet sheet;
                //每个sheet最多65534行
                int elementMax = data.getSize();
                if(elementMax > 0) {
                    int sheetMax = elementMax / 65534;
                    sheetMax += 1;
                    int index = 0;
                    for(int i=0;i<sheetMax;i++){
                        sheet = wwb.createSheet("Sheet"+String.valueOf(i+1),i);
                        addTitle(sheet);
                        if((elementMax-index)>= 65534){
                            addOneSheet(sheet,data.getDate(),data.getData(),index,index+65534);
                            index += 65534;
                        }else{
                            addOneSheet(sheet,data.getDate(),data.getData(),index,elementMax);
                            break;
                        }
                    }
                }else{
                    sheet = wwb.createSheet("Sheet1",0);
                    addTitle(sheet);
                }

                wwb.write();
                os.flush();
                wwb.close();
                //需要关闭输出流，结束占用，否则系统会 结束 app
                os.close();

            }catch (IOException e) {
                e.printStackTrace();
                exportDataResult = false;
            } catch (RowsExceededException e) {
                e.printStackTrace();
                exportDataResult = false;
            } catch (WriteException e) {
                e.printStackTrace();
                exportDataResult = false;
            }
            return exportDataResult;

        }

        private void addTitle(WritableSheet sheet) throws WriteException {
            Label label;
            label = new Label(0,0,"时间");
            sheet.addCell(label);
            label = new Label(1,0,"扬尘 mg/m³");
            sheet.addCell(label);
            label = new Label(2,0,"温度 ℃");
            sheet.addCell(label);
            label = new Label(3,0,"湿度 %");
            sheet.addCell(label);
            label = new Label(4,0,"气压 hPa");
            sheet.addCell(label);
            label = new Label(5,0,"风速 m/s");
            sheet.addCell(label);
            label = new Label(6,0,"风向 °");
            sheet.addCell(label);
            label = new Label(7,0,"噪声 dB");
            sheet.addCell(label);
        }

        private void addOneSheet(WritableSheet sheet, List<String> date,List<List<String>> data, int index, int max) throws WriteException {
            int row=1;
            List<String> element;
            for(int i=index;i<max;i++){
                element = data.get(i);
                Label label;
                label = new Label(0,row,date.get(i));
                sheet.addCell(label);
                for(int j=0;j<7;j++){
                    label = new Label(j+1,row,element.get(j));
                    sheet.addCell(label);
                }
                row++;
            }
        }
    }

    public boolean hasDataTable(String id){
        DbTask dbTask = new DbTask(context,1);
        SQLiteDatabase db = dbTask.getWritableDatabase();
        String tableName = "data_"+id;
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name",null);
        while (cursor.moveToNext()){
            Log.d(tag,cursor.getString(0));
            if(cursor.getString(0).equals(tableName)){
                dbTask.close();
                db.close();
                return true;
            }
        }
        db.close();
        dbTask.close();
        return false;
    }

    public void loadDatFromDatabase(String id){
        DbTask dbTask = new DbTask(context,1);
        SQLiteDatabase db = dbTask.getWritableDatabase();
        String tableName = "data_"+id;
        Cursor cursor = db.rawQuery("SELECT * FROM "+tableName+" ORDER BY date desc",null);
        GeneralMinData minData;
        while (cursor.moveToNext()){
            minData = new GeneralMinData();
            minData.setDate(cursor.getLong(0));
            minData.setDust(cursor.getFloat(1));
            minData.setTemperate(cursor.getFloat(3));
            minData.setHumidity(cursor.getFloat(4));
            minData.setPressure(cursor.getFloat(5));
            minData.setWindForce(cursor.getFloat(6));
            minData.setWindDirection(cursor.getFloat(7));
            minData.setNoise(cursor.getFloat(8));
            historyData.add(minData);
        }
        cursor.close();
        db.close();
        dbTask.close();
        dataSearchListener.showAllData(historyData.getDate(),historyData.getData());

    }

    @Override
    public void setHistoryData() {
        Log.d(tag,"收到新的历史数据");
        hasNewData = true;
    }

    public void readyToSearchData(long start,long end,int name){
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
        dataTileName = name;
        new GetHistoryDataThread(this,name).start();

    }



    private class GetHistoryDataThread extends Thread{
        private long index;
        private HistoryDataListener listener;
        private long SearchInterval = 900000l;
        private int titleName = 0;

        public GetHistoryDataThread(HistoryDataListener listener,int dataTitleName){
            index = startDate;
            this.listener = listener;
            if(dataTitleName == 1){
                SearchInterval = 12*3600000l;
            }

            titleName = dataTitleName;
        }
        @Override
        public void run() {
            //每次搜索0.25小时数据,搜索完成后显示结果
            long next = index + SearchInterval;
            int times = 0;
            historyData.clear();
            int process;// = (int) ((next - startDate)*100/(endDate - startDate));

            while (next <= endDate) {
                hasNewData = false;
                if(titleName == 1){
                   state.getHistoryHourData(index,next,listener,historyData);
                }else {
                    state.getHistoryData(index, next, listener, historyData);
                }
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(hasNewData){
                    process = (int) ((next - startDate)*100/(endDate - startDate));
                    dataSearchListener.showDataDownLoadProcess(process);
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
            dataSearchListener.showDataDownLoadProcess(99);
            if(hasNewData){
                if(index < endDate){
                    if(titleName == 1){
                        state.getHistoryHourData(index,endDate,listener,historyData);
                    }else {
                        state.getHistoryData(index, endDate, listener, historyData);
                    }
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
