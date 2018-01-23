package com.grean.dustguest.presenter;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.grean.dustguest.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by weifeng on 2018/1/22.
 */

public class LogActivity extends Activity implements AbsListView.OnScrollListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lv;
    private ArrayAdapter adapter;
    private List<String> list;
    private View footerView;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏

        findViewById(R.id.log_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_srl);
        lv = (ListView) findViewById(R.id.main_lv);

        footerView = getLayoutInflater().inflate(R.layout.menu_layout,null);
        lv.addFooterView(footerView);

        lv.setOnScrollListener(this);

        list = new ArrayList<>();
        list.addAll(Arrays.asList("Java","php","C++","C#","IOS","html","C","J2ee","j2se","VB",".net","Http","tcp","udp","www"));

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,
                android.R.id.text1,list);
        lv.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadDataThread().start();
            }
        });
    }

    private android.os.Handler handler = new android.os.Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x101:
                    if (swipeRefreshLayout.isRefreshing()){
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    }
                    break;
            }
        }
    };

    private int visibleLastIndex;//用来可显示的最后一条数据的索引
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(adapter.getCount() == visibleLastIndex && i == SCROLL_STATE_IDLE){
            new LoadDataThread().start();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        visibleLastIndex = i + i1 - 1;//减去最后一个加载中那条
    }

    class LoadDataThread extends  Thread{
        @Override
        public void run() {
            initData();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0x101);//通过handler发送一个更新数据的标记
        }

        private void initData() {
            list.addAll(Arrays.asList("Json","XML","UDP","http"));
        }
    }
}
