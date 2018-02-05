package com.grean.dustguest.presenter;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grean.dustguest.R;
import com.grean.dustguest.model.SearchLog;
import com.tools;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by weifeng on 2018/1/22.
 */

public class LogActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,LogSearchListener,View.OnClickListener{//AbsListView.OnScrollListener ,LogSearchListener,View.OnClickListener{
    private static final String tag = "LogActivity";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lv;
    private ArrayAdapter adapter;
    private List<String> list;
    //private View footerView;//底部view，需要时加上
    private TextView tvStartDate,tvEndDate;
    private Button btnSaveLog;
    private EndDialogTimeSelected endDialogTimeSelected = new EndDialogTimeSelected();
    private StartDialogTimeSelected startDialogTimeSelected =new StartDialogTimeSelected();
    private SearchLog log;
    private AlertDialog dialog;
    private String fileName,idString;
    private static final int msgRefresh=1,msgGetNew=2,msgSuccessToSaveFile =3,msgFailToSaveFile =4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止自动锁屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        setContentView(R.layout.activity_log);

        tvEndDate = findViewById(R.id.tvLogEnd);
        tvStartDate = findViewById(R.id.tvLogStart);
        btnSaveLog = findViewById(R.id.btnSaveLogToLocal);
        btnSaveLog.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        long now = tools.nowtime2timestamp();
        tvEndDate.setText(tools.timestamp2string(now));
        tvStartDate.setText(tools.timestamp2string(now-3600000l*24));

        findViewById(R.id.log_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_srl);
        lv = (ListView) findViewById(R.id.main_lv);

       // footerView = getLayoutInflater().inflate(R.layout.menu_layout,null);
        //lv.addFooterView(footerView);

        //lv.setOnScrollListener(this);


        list = new ArrayList<>();
        //list.addAll(Arrays.asList("Java","php","C++","C#","IOS","html","C","J2ee","j2se","VB",".net","Http","tcp","udp","www"));

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,
                android.R.id.text1,list);
        lv.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadDataThread().start();
            }
        });*/
        log = new SearchLog(this,this);
        idString = getIntent().getStringExtra("id");
        if(idString==null){
            idString = "TestID";
        }
    }

    private android.os.Handler handler = new android.os.Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what){
                case msgRefresh:
                    if (swipeRefreshLayout.isRefreshing()){
                        Log.d(tag,"刷新"+String.valueOf(list.size()));
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    }
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
                case msgGetNew:
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
                case msgSuccessToSaveFile:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(LogActivity.this,"保存成功,文件路径为: "+fileName,Toast.LENGTH_SHORT).show();
                    break;
                case msgFailToSaveFile:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    Toast.makeText(LogActivity.this,"保存失败!  "+fileName,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };



    /*private int visibleLastIndex;//用来可显示的最后一条数据的索引
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(adapter.getCount() == visibleLastIndex && i == SCROLL_STATE_IDLE){
            new LoadDataThread().start();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        visibleLastIndex = i + i1 - 1;//减去最后一个加载中那条
    }*/

    @Override
    public void showNewLog(List<String> log) {
        Log.d(tag,"显示新日志");
        list.clear();
        for(int i=0;i<log.size();i++){
            list.add(log.get(i));
        }
        handler.sendEmptyMessage(msgGetNew);
    }

    @Override
    public void showRefreshLog(List<String> log,int logIndex) {
        for(int i=logIndex;i<log.size();i++){
            list.add(0,log.get(i));
        }
        handler.sendEmptyMessage(msgRefresh);
    }

    @Override
    public void saveLogComplete(boolean success, String fileName) {
        if(success){
            handler.sendEmptyMessage(msgSuccessToSaveFile);
        }else{
            handler.sendEmptyMessage(msgFailToSaveFile);
        }
        this.fileName = fileName;
    }

    @Override
    public void onClick(View v) {
        Calendar calendar;
        switch(v.getId()){
            case R.id.tvLogStart:
                calendar = Calendar.getInstance();
                new DialogTimeChoose(this,"查询起始时间")
                        .showDialog(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,startDialogTimeSelected);
                break;
            case R.id.tvLogEnd:
                calendar = Calendar.getInstance();
                new DialogTimeChoose(this,"查询结束时间")
                        .showDialog(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),endDialogTimeSelected);
                break;
            case R.id.btnSaveLogToLocal:
                final ProgressBar pb = new ProgressBar(this);
                dialog = new AlertDialog.Builder(this).setTitle("正在保存日志").setView(pb).setCancelable(false).show();
                log.saveLogToFile(idString);
                break;
            default:

                break;
        }

    }

    @Override
    public void onRefresh() {
        Log.d(tag,"刷新");
        long start = tools.string2timestamp(tvStartDate.getText().toString());
        long end = tools.string2timestamp(tvEndDate.getText().toString());
        end = log.refreshLog(start,end);
        //Log.d(tag,"start = "+tools.timestamp2string(start)+";end = "+tools.timestamp2string(end));
        tvEndDate.setText(tools.timestamp2string(end));
    }

   /* class LoadDataThread extends  Thread{
        @Override
        public void run() {
            initData();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(msgRefresh);//通过handler发送一个更新数据的标记
        }

        private void initData() {
            list.addAll(Arrays.asList("Json","XML","UDP","http"));
        }
    }*/

    private class StartDialogTimeSelected implements DialogTimeSelected{

        @Override
        public void onComplete(String string) {
            long time = tools.string2timestamp(string);
            long now = tools.nowtime2timestamp();
            if(time >= now){
                time = now - 3600000l*24;
                tvStartDate.setText(tools.timestamp2string(time));
            }else{
                tvStartDate.setText(string);
            }
            long end = tools.string2timestamp(tvEndDate.getText().toString());
            if(end >= now){
                end = now;
                tvEndDate.setText(tools.timestamp2string(end));
            }
            final ProgressBar pb = new ProgressBar(LogActivity.this);
            dialog = new AlertDialog.Builder(LogActivity.this).setTitle("正在查询日志").setView(pb).setCancelable(true).show();
            log.getLog(time,end);
            //listener.searchData(time,end);
        }
    }

    private class EndDialogTimeSelected implements DialogTimeSelected{


        @Override
        public void onComplete(String string) {
            long time = tools.string2timestamp(string);
            long now = tools.nowtime2timestamp();
            if(time >= now){
                time = now;
                tvEndDate.setText(tools.timestamp2string(time));
            }else{
                tvEndDate.setText(string);
            }
            final ProgressBar pb = new ProgressBar(LogActivity.this);
            dialog = new AlertDialog.Builder(LogActivity.this).setTitle("正在查询日志").setView(pb).setCancelable(true).show();
            log.getLog(tools.string2timestamp(tvStartDate.getText().toString()),time);
            //listener.searchData(tools.string2timestamp(tvDataStart.getText().toString()),time);
        }
    }
}
