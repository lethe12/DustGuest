package com.grean.dustguest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.curve.HealthyTablesView;
import com.google.zxing.StartScanForResult;
import com.google.zxing.activity.CaptureActivity;
import com.grean.dustguest.model.LastDataInfo;
import com.grean.dustguest.model.LastDevicesInfo;
import com.grean.dustguest.model.LocalServerListener;
import com.grean.dustguest.model.LocalServerManager;
import com.grean.dustguest.presenter.DataActivity;
import com.grean.dustguest.presenter.DialogProcessFragmentBarStyle;
import com.grean.dustguest.presenter.PopWindow;
import com.grean.dustguest.presenter.PopWindowListener;
import com.grean.dustguest.presenter.RealTimeDataDisplay;
import com.grean.dustguest.protocol.ProtocolLib;
import com.grean.dustguest.protocol.RealTimeDataFormat;
import com.tools;
import com.utils.CommonUtil;
import com.wifi.WifiAdmin;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,StartScanForResult,PopWindowListener,LocalServerListener,RealTimeDataDisplay{
    private static final String tag = "MainActivity";
    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;
    private LastDevicesInfo lastDevicesInfo;
    private HealthyTablesView tablesView;
    private TextView [] tvNames = new TextView[8],tvValues = new TextView[8],tvUnits=new TextView[8];
    private View [] layouts = new View[7];
    //@BindView(R.id.btnTestScan)private Button btnTestScan;
    private TextView tvTableInfo,tvState,tvLocalServer,tvScanResult;
    private LocalServerManager localServerManager;
    private LastDataInfo dataInfo;
    private DialogProcessFragmentBarStyle dialog;
   // private ProgressBar pb;
    private boolean connectResult;
    private RealTimeDataFormat dataFormat;
    private String dustName,idString;
    private boolean menuEnable = false;
    private static final int msgConnectResult = 1,msgShowRealTimeData=2,msgShowDustName=3,
    msgDisconnect = 4,msgOfflineDevices = 5;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgConnectResult:
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    if(connectResult){
                        tvLocalServer.setText("已连接设备");
                        tvScanResult.setText("设备ID:  "+idString);
                        Toast.makeText(MainActivity.this,"已连接成功！",Toast.LENGTH_SHORT).show();
                    }else{
                        tvLocalServer.setText("未连接设备");
                        Toast.makeText(MainActivity.this,"连接失败，请重试！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case msgDisconnect:
                    tvLocalServer.setText("未连接设备");
                    tvScanResult.setText("设备ID:");
                    tvState.setText("当前状态:");
                    break;
                case msgShowRealTimeData:
                    String serverConnectString;
                    if(dataFormat.isServerConnected()){
                        serverConnectString = "  已连接服务器";
                    }else{
                        serverConnectString = "  未连接服务器";
                    }
                    tvState.setText("当前状态:"+dataFormat.getState()+serverConnectString);
                    if(dataFormat.isAlarm()){
                        layouts[0].setBackgroundColor(getColor(R.color.red));
                    }else{
                        layouts[0].setBackgroundColor(getColor(R.color.background));
                    }
                    tvValues[0].setText(tools.float2String3(dataFormat.getDust()));
                    tvValues[1].setText(tools.float2String1(dataFormat.getTemperature()));
                    tvValues[2].setText(tools.float2String1(dataFormat.getHumidity()));
                    tvValues[3].setText(tools.float2String0(dataFormat.getPressure()));
                    tvValues[4].setText(tools.float2String1(dataFormat.getWindForce()));
                    tvValues[5].setText(tools.float2String0(dataFormat.getWindDirection())+" "
                            +tools.windDirection2String(dataFormat.getWindDirection()));
                    tvValues[6].setText(tools.float2String1(dataFormat.getNoise()));
                    break;
                case msgShowDustName:
                    tvNames[0].setText(dustName);
                    break;
                case msgOfflineDevices:
                    tvScanResult.setText("设备ID:   "+idString);
                    tvState.setText("当前状态:");
                    break;
                default:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止自动锁屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏

        setContentView(R.layout.activity_main);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        /*WifiAdmin wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("GreanDust","1234567890",3));*/

        findViewById(R.id.btnMoreFunction).setOnClickListener(this);
        lastDevicesInfo = new LastDevicesInfo(this);
        localServerManager = new LocalServerManager(this,this);
        ProtocolLib.getInstance().getClientProtocol().setRealTimeDisplay(this);
        dataInfo = new LastDataInfo(this);
        Log.d(tag,"重载activity");
        //tablesView.invalidate();//跟新view
    }

    private void initView(){
        tvScanResult = findViewById(R.id.tvScanResult);
        tablesView = findViewById(R.id.tableView);
        tvTableInfo = findViewById(R.id.tvTableInfo);
        tvState = findViewById(R.id.tvState);
        tvLocalServer = findViewById(R.id.tvLocalServerState);
        tvNames[0] = findViewById(R.id.tvName1);
        tvNames[1] = findViewById(R.id.tvName2);
        tvNames[2] = findViewById(R.id.tvName3);
        tvNames[3] = findViewById(R.id.tvName4);
        tvNames[4] = findViewById(R.id.tvName5);
        tvNames[5] = findViewById(R.id.tvName6);
        tvNames[6] = findViewById(R.id.tvName7);
        tvNames[7] = findViewById(R.id.tvName8);
        tvValues[0] = findViewById(R.id.tvValue1);
        tvValues[1] = findViewById(R.id.tvValue2);
        tvValues[2] = findViewById(R.id.tvValue3);
        tvValues[3] = findViewById(R.id.tvValue4);
        tvValues[4] = findViewById(R.id.tvValue5);
        tvValues[5] = findViewById(R.id.tvValue6);
        tvValues[6] = findViewById(R.id.tvValue7);
        tvValues[7] = findViewById(R.id.tvValue8);
        tvUnits[0] = findViewById(R.id.tvUnit1);
        tvUnits[1] = findViewById(R.id.tvUnit2);
        tvUnits[2] = findViewById(R.id.tvUnit3);
        tvUnits[3] = findViewById(R.id.tvUnit4);
        tvUnits[4] = findViewById(R.id.tvUnit5);
        tvUnits[5] = findViewById(R.id.tvUnit6);
        tvUnits[6] = findViewById(R.id.tvUnit7);
        tvUnits[7] = findViewById(R.id.tvUnit8);
        layouts[0] = findViewById(R.id.layout1);
        layouts[0].setOnClickListener(this);
        layouts[1] = findViewById(R.id.layout2);
        layouts[1].setOnClickListener(this);
        layouts[2] = findViewById(R.id.layout3);
        layouts[2].setOnClickListener(this);
        layouts[3] = findViewById(R.id.layout4);
        layouts[3].setOnClickListener(this);
        layouts[4] = findViewById(R.id.layout5);
        layouts[4].setOnClickListener(this);
        layouts[5] = findViewById(R.id.layout6);
        layouts[5].setOnClickListener(this);
        layouts[6] = findViewById(R.id.layout7);
        layouts[6].setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Log.d(tag,"回调");
        super.onActivityResult(requestCode, resultCode, data);
//扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("qr_scan_result");
            //将扫描出的信息显示出来
            //Log.d(tag,scanResult);
            idString = scanResult;
            //tvScanResult.setText(scanResult);
            startNewLocalServer(idString);
        }
    }

    @Override
    protected void onDestroy() {
        localServerManager.stopScan();//停止扫描，防止幽灵进程
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnMoreFunction:
                PopWindow popWindow = new PopWindow(this,this,this,menuEnable,localServerManager.isConnect(),idString);
                popWindow.showPopupWindow(findViewById(R.id.btnMoreFunction));
                break;
            case R.id.layout1:
                if(localServerManager.isConnect()) {
                    tvTableInfo.setText(dustName);
                    dataInfo.getLastMinData(LastDataInfo.Dust);
                }
                break;
            case R.id.layout2:
                if(localServerManager.isConnect()) {
                    tvTableInfo.setText("温度 单位:℃");
                    dataInfo.getLastMinData(LastDataInfo.Temperate);
                }
                break;
            case R.id.layout3:
                if(localServerManager.isConnect()) {
                    tvTableInfo.setText("湿度 单位:%");
                    dataInfo.getLastMinData(LastDataInfo.Humidity);
                }
                break;
            case R.id.layout4:
                if(localServerManager.isConnect()) {
                    tvTableInfo.setText("大气压 单位:hPa");
                    dataInfo.getLastMinData(LastDataInfo.Pressure);
                }
                break;
            case R.id.layout5:
                if(localServerManager.isConnect()) {
                    tvTableInfo.setText("风速 单位:m/s");
                    dataInfo.getLastMinData(LastDataInfo.WindForce);
                }
                break;
            case R.id.layout6:
                if(localServerManager.isConnect()) {
                    tvTableInfo.setText("风向 单位:°");
                    dataInfo.getLastMinData(LastDataInfo.WindDirection);
                }
                break;
            case R.id.layout7:
                if(localServerManager.isConnect()) {
                    tvTableInfo.setText("噪声 单位:dB");
                    dataInfo.getLastMinData(LastDataInfo.Noise);
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void startScan() {
        if(CommonUtil.isCameraCanUse()){
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }else{
            Toast.makeText(this,"摄像头权限",Toast.LENGTH_LONG).show();
        }
    }

    private void startNewLocalServer(String id){
        //pb = new ProgressBar(this);
        //dialog = new AlertDialog.Builder(this).setTitle("正在连接设备").setView(pb).setCancelable(false).show();
        dialog = new DialogProcessFragmentBarStyle();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(),"startLocalServer");

        dialog.showInfo("正在连接设备");
        Log.d(tag,"获取ID为:"+id);
        tvScanResult.setText("设备ID:");
        tvState.setText("当前状态:");
        localServerManager.startLocalServer(id,dialog);
    }

    @Override
    public void OnInputIdComplete(String string) {
        startNewLocalServer(string);
        idString = string;
        //tvScanResult.setText("设备ID: "+idString);

    }

    @Override
    public String[] getLastIdList() {
        return lastDevicesInfo.getLastDevicesList();
    }



    @Override
    public void OnLocalServerResult(boolean result) {
        connectResult = result;
        if(result){
            menuEnable = true;
            lastDevicesInfo.saveConfig();
        }else{//未连接设备
            if(localServerManager.hasDevicesInfo(idString)){//已连接过的设备
                menuEnable = true;
                lastDevicesInfo.loadDeviceConfig(localServerManager.getDustName(),
                        localServerManager.getConfig(),localServerManager.getDustMeterInfo());
                handler.sendEmptyMessage(msgOfflineDevices);
            }else{
                menuEnable = false;
            }
        }
        handler.sendEmptyMessage(msgConnectResult);
    }

    @Override
    public void OnDisconnectServer() {
        Log.d(tag,"断内网");
        if(connectResult) {//已经连接的
            connectResult = false;
            handler.sendEmptyMessage(msgDisconnect);
        }

    }


    @Override
    public void show(RealTimeDataFormat format) {
        this.dataFormat = format;
        handler.sendEmptyMessage(msgShowRealTimeData);
    }

    @Override
    public void showDustName(String name) {
        dustName = name;
        handler.sendEmptyMessage(msgShowDustName);
    }

    @Override
    public void showLastData(String[] date, float[] data) {
        tablesView.updateTableData(date,data);
        tablesView.postInvalidate();
    }

}
