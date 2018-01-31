package com.grean.dustguest.presenter;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ScrollablePanel.ElementInfo;
import com.ScrollablePanel.HistoryDataPanelAdapter;
import com.ScrollablePanel.ScrollablePanel;
import com.grean.dustguest.R;
import com.grean.dustguest.model.SearchData;
import com.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by weifeng on 2018/1/22.
 */

public class DataActivity extends Activity implements View.OnClickListener ,DataSearchListener{
    private static final String tag = "DataActivity";
    private TextView tvDataStart,tvDataEnd;
    private Button btnSaveDataToLocal;
    private HistoryDataPanelAdapter historyDataPanelAdapter;
    private ScrollablePanel scrollablePanel;
    /*private List<String> date;
    private List<List<String>> data;*/
    private AlertDialog dialog;
    private SearchData searchData;
    private EndDialogTimeSelected endDialogTimeSelected;
    private StartDialogTimeSelected startDialogTimeSelected;
    private static final String[] elementNames = {"扬尘","温度","湿度","气压","风速","风向","噪声"},
            getElementUnit = {"mg/m³","℃","%","hPa","m/s","°","dB"};
    private static final int msgShowAllHistory = 1,msgNoneData = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgShowAllHistory:
                    scrollablePanel.setPanelAdapter(historyDataPanelAdapter);
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
                case msgNoneData:
                    Toast.makeText(DataActivity.this,"该时段没有历史数据",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止自动锁屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        setContentView(R.layout.activity_data);
        tvDataStart = findViewById(R.id.tvDataStart);
        tvDataEnd = findViewById(R.id.tvDataEnd);
        btnSaveDataToLocal = findViewById(R.id.btnSaveDataToLocal);
        scrollablePanel = findViewById(R.id.scrollable_panel);
        tvDataEnd.setOnClickListener(this);
        tvDataStart.setOnClickListener(this);
        btnSaveDataToLocal.setOnClickListener(this);
        historyDataPanelAdapter = new HistoryDataPanelAdapter();
        setElement(historyDataPanelAdapter);
        scrollablePanel.setPanelAdapter(historyDataPanelAdapter);
        long now = tools.nowtime2timestamp();
        tvDataEnd.setText(tools.timestamp2string(now));
        tvDataStart.setText(tools.timestamp2string(now - 3600000l));
        searchData = new SearchData(this);
        startDialogTimeSelected = new StartDialogTimeSelected(this);
        endDialogTimeSelected = new EndDialogTimeSelected(this);
        findViewById(R.id.data_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Calendar calendar;
        switch (v.getId()){
            case R.id.tvDataStart:
                calendar = Calendar.getInstance();
                new DialogTimeChoose(this,"查询起始时间")
                        .showDialog(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),0,startDialogTimeSelected);
                break;
            case R.id.tvDataEnd:
                calendar = Calendar.getInstance();
                new DialogTimeChoose(this,"查询结束时间")
                        .showDialog(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),endDialogTimeSelected);
                break;
            case R.id.btnSaveDataToLocal:

                break;
            default:
                break;
        }
    }

    private void setElement(HistoryDataPanelAdapter historyDataPanelAdapter){
        List<ElementInfo> element = new ArrayList<>();
        for (int i=0;i<7;i++) {
            ElementInfo info = new ElementInfo();
            info.setName(elementNames[i]);
            info.setUnit(getElementUnit[i]);
            element.add(info);
        }
        historyDataPanelAdapter.setElement(element);

        List<String>date = new ArrayList<>();
        for(int i=0;i<1;i++){
            String dateString = "-";
            date.add(dateString);
        }
        historyDataPanelAdapter.setDate(date);

        List<List<String>> data = new ArrayList<>();
        for(int i =0;i<1;i++){
            List<String> item = new ArrayList<>();
            for(int j=0;j<7;j++){
                String string = "-";
                item.add(string);
            }
            data.add(item);
        }
        historyDataPanelAdapter.setData(data);
    }

    @Override
    public void searchData(long start, long end) {
        final ProgressBar pb = new ProgressBar(this);
        dialog = new AlertDialog.Builder(this).setTitle("正在查询历史数据").setView(pb).setCancelable(true).show();
        searchData.readyToSearchData(start,end);
    }

    @Override
    public void showAllData(List<String> date, List<List<String>> data) {
        if((data!=null)&&(date!=null)) {
            if(date.size()>0) {
                historyDataPanelAdapter.setDate(date);
                historyDataPanelAdapter.setData(data);
                handler.sendEmptyMessage(msgShowAllHistory);
            }else{
                handler.sendEmptyMessage(msgNoneData);
            }
        }else{
            handler.sendEmptyMessage(msgNoneData);
        }
    }

    private class StartDialogTimeSelected implements DialogTimeSelected{

        private DataSearchListener listener;

        public StartDialogTimeSelected(DataSearchListener listener){
            this.listener = listener;
        }
        @Override
        public void onComplete(String string) {
            long time = tools.string2timestamp(string);
            long now = tools.nowtime2timestamp();
            if(time >= now){
                time = now - 3600000l;
                tvDataStart.setText(tools.timestamp2string(time));
            }else{
                tvDataStart.setText(string);
            }
            long end = tools.string2timestamp(tvDataEnd.getText().toString());
            if(end >= now){
                end = now;
                tvDataEnd.setText(tools.timestamp2string(end));
            }
            listener.searchData(time,end);
        }
    }

    private class EndDialogTimeSelected implements DialogTimeSelected{

        private DataSearchListener listener;

        public EndDialogTimeSelected(DataSearchListener listener){
            this.listener = listener;
        }
        @Override
        public void onComplete(String string) {
            long time = tools.string2timestamp(string);
            long now = tools.nowtime2timestamp();
            if(time >= now){
                time = now;
                tvDataEnd.setText(tools.timestamp2string(time));
            }else{
                tvDataEnd.setText(string);
            }
            listener.searchData(tools.string2timestamp(tvDataStart.getText().toString()),time);
        }
    }

}
