package com.grean.dustguest.presenter;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.grean.dustguest.R;
import com.grean.dustguest.model.SettingManager;
import com.grean.dustguest.protocol.GeneralConfig;
import com.grean.dustguest.protocol.ProtocolLib;
import com.grean.dustguest.protocol.RealTimeDataFormat;
import com.tools;

/**
 * Created by weifeng on 2018/1/22.
 */

public class SettingActivity extends Activity implements View.OnClickListener,AdapterView.OnItemSelectedListener,SettingManagerListener,RealTimeSettingDisplay{
    private EditText etDustParaK,etAutoInterval,etMotorStep,etMotorTime,etAlarmValue,etMnCode,etServerIp,etServerPort,etPassword,etUpdateUrl;
    private Switch swAutoCalEnable,swDustMeter,swRelay1,swRelay2,swRelay3,swRelay4,swRelay5;
    private TextView tvAutoCalTime,tvDustMeterInfo,tvRealTimeState;
    private TextView[] tvRealTimeValue=new TextView[16];
    private Spinner spDustName,spProtocols;
    private LinearLayout motorSet1,MotorSet2,RelaySet,passwordSet,realTimeDisplay;
    private Button btnSaveDustPara;
    private SettingManager manager;
    private int dustName,protocolName;
    private RealTimeDataFormat format;
    private static final int msgShowRealTime = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msgShowRealTime:
                    if(format!=null) {
                        tvRealTimeValue[0].setText(tools.float2String3(format.getDust()));
                        tvRealTimeValue[1].setText(tools.float2String3(format.getValue()));
                        tvRealTimeValue[2].setText(tools.float2String3(format.getTemperature()));
                        tvRealTimeValue[3].setText(tools.float2String3(format.getHumidity()));
                        tvRealTimeValue[4].setText(tools.float2String3(format.getPressure()));
                        tvRealTimeValue[5].setText(tools.float2String1(format.getWindForce()));
                        tvRealTimeValue[6].setText(tools.float2String3(format.getWindDirection()));
                        tvRealTimeValue[7].setText(tools.float2String3(format.getNoise()));
                        tvRealTimeValue[8].setText(tools.float2String3(format.getExitTemperature()));
                        tvRealTimeValue[9].setText(tools.float2String3(format.getExitHumidity()));
                        tvRealTimeValue[10].setText(tools.float2String3(format.getEntranceDewPoint()));
                        tvRealTimeValue[11].setText(tools.float2String3(format.getExitDewPoint()));
                        tvRealTimeValue[12].setText(tools.float2String3(format.getHeatParams()));
                        tvRealTimeValue[13].setText(String.valueOf(format.isBatteryLow()));
                        tvRealTimeValue[14].setText(String.valueOf(format.isAcOk()));
                        tvRealTimeState.setText(format.getState());
                        swRelay1.setChecked(format.getRelays(1));
                        swRelay2.setChecked(format.getRelays(2));
                        swRelay3.setChecked(format.getRelays(3));
                        swRelay4.setChecked(format.getRelays(4));
                        swRelay5.setChecked(format.getRelays(5));
                        swDustMeter.setChecked(format.isDustMeterRun());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        setContentView(R.layout.activity_setting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        findViewById(R.id.setting_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        if(getIntent().getBooleanExtra("isAdvance",false)){//高级设置
            etDustParaK.setEnabled(true);
            btnSaveDustPara.setEnabled(true);
        }else{
            etDustParaK.setEnabled(false);
            btnSaveDustPara.setEnabled(false);
        }

        manager = new SettingManager(this);
        manager.loadSetting();
        ProtocolLib.getInstance().getClientProtocol().setRealTimeSettingDisplay(this);
    }

    private void initView(){
        etDustParaK = findViewById(R.id.etOperateDustParaK);
        etAutoInterval = findViewById(R.id.etOperateAutoCalInterval);
        etMotorStep = findViewById(R.id.etMotorSetp);
        etMotorTime = findViewById(R.id.etMotorTime);
        etAlarmValue = findViewById(R.id.etOperateAlarm);
        etMnCode = findViewById(R.id.etOperateMnCode);
        etServerIp = findViewById(R.id.etOperateServerIp);
        etServerPort = findViewById(R.id.etOperateServerPort);
        etUpdateUrl = findViewById(R.id.etOperateUpdateSoftwareUrl);
        swAutoCalEnable = findViewById(R.id.swOperateAutoCal);
        swDustMeter = findViewById(R.id.swDustMeter);
        swRelay1 = findViewById(R.id.swRelay1);
        swRelay2 = findViewById(R.id.swRelay2);
        swRelay3 = findViewById(R.id.swRealy3);
        swRelay4 = findViewById(R.id.swRelay4);
        swRelay5 = findViewById(R.id.swRelay5);
        spDustName = findViewById(R.id.spDustType);
        spProtocols = findViewById(R.id.spOperateProticol);
        tvRealTimeValue[0] = findViewById(R.id.tvRealTimeValue1);
        tvRealTimeValue[1] = findViewById(R.id.tvRealTimeValue2);
        tvRealTimeValue[2] = findViewById(R.id.tvRealTimeValue3);
        tvRealTimeValue[3] = findViewById(R.id.tvRealTimeValue4);
        tvRealTimeValue[4] = findViewById(R.id.tvRealTimeValue5);
        tvRealTimeValue[5] = findViewById(R.id.tvRealTimeValue6);
        tvRealTimeValue[6] = findViewById(R.id.tvRealTimeValue7);
        tvRealTimeValue[7] = findViewById(R.id.tvRealTimeValue8);
        tvRealTimeValue[8] = findViewById(R.id.tvRealTimeValue9);
        tvRealTimeValue[9] = findViewById(R.id.tvRealTimeValue10);
        tvRealTimeValue[10] = findViewById(R.id.tvRealTimeValue11);
        tvRealTimeValue[11] = findViewById(R.id.tvRealTimeValue12);
        tvRealTimeValue[12] = findViewById(R.id.tvRealTimeValue13);
        tvRealTimeValue[13] = findViewById(R.id.tvRealTimeValue14);
        tvRealTimeValue[14] = findViewById(R.id.tvRealTimeValue15);
        tvRealTimeValue[15] = findViewById(R.id.tvRealTimeValue16);

        tvRealTimeState = findViewById(R.id.tvSettingState);
        tvDustMeterInfo = findViewById(R.id.tvOperateDustMeterInfo);
        tvAutoCalTime = findViewById(R.id.tvOperateAutoCalDate);
        tvAutoCalTime.setOnClickListener(this);
        btnSaveDustPara = findViewById(R.id.btnOperateDustSetParaK);
        btnSaveDustPara.setOnClickListener(this);
        findViewById(R.id.btnOperateCalMan).setOnClickListener(this);
        findViewById(R.id.btnSaveDustType).setOnClickListener(this);
        findViewById(R.id.btnSaveMotorParams).setOnClickListener(this);
        findViewById(R.id.btnOperateSaveServer).setOnClickListener(this);
        findViewById(R.id.btnOperateUpdateSoftware).setOnClickListener(this);
        findViewById(R.id.btnOperateUpdateSetting).setOnClickListener(this);
        findViewById(R.id.btnSavePassword).setOnClickListener(this);
        findViewById(R.id.btnOperateVideoSetting).setOnClickListener(this);
        findViewById(R.id.btnForwardTest).setOnClickListener(this);
        findViewById(R.id.btnBackwardTest).setOnClickListener(this);
        findViewById(R.id.btnForwardStep).setOnClickListener(this);
        findViewById(R.id.btnBackwardStep).setOnClickListener(this);
        swAutoCalEnable.setOnClickListener(this);
        swDustMeter.setOnClickListener(this);
        swRelay1.setOnClickListener(this);
        swRelay2.setOnClickListener(this);
        swRelay3.setOnClickListener(this);
        swRelay4.setOnClickListener(this);
        swRelay5.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void showContent(GeneralConfig config) {
        etDustParaK.setText(tools.float2String4(config.getDustParaK()));
        swAutoCalEnable.setChecked(config.isAutoCalEnable());
        tvAutoCalTime.setText(tools.timestamp2string(config.getAutoCalTime()));
        etAutoInterval.setText(String.valueOf(config.getAutoCalInterval()/3600000l));
        if(!config.isAutoCalEnable()){
            tvAutoCalTime.setVisibility(View.INVISIBLE);
            etAutoInterval.setVisibility(View.INVISIBLE);
        }
        tvDustMeterInfo.setText("气泵累计运行时间"+String.valueOf(config.getDustMeterPumpTime())+
                "小时,激光器累计运行时间"+String.valueOf(config.getDustMeterLaserTime())+"小时");
        ArrayAdapter<String> adapterDustNames = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,config.getDustNames());
        spDustName.setAdapter(adapterDustNames);
        dustName = config.getDustName();
        spDustName.setSelection(dustName);
        spDustName.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapterProtocols = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,config.getClientProtocolNames());
        spProtocols.setAdapter(adapterProtocols);
        protocolName = config.getClientProtocolName();
        spProtocols.setSelection(protocolName);
        spProtocols.setOnItemSelectedListener(this);

        etMotorStep.setText(String.valueOf(config.getMotorStep()));
        etMotorTime.setText(String.valueOf(config.getMotorTime()));
        etAlarmValue.setText(String.valueOf(config.getAlarmDust()));
        etMnCode.setText(config.getMnCode());
        etServerIp.setText(config.getServerIp());
        etServerPort.setText(String.valueOf(config.getServerPort()));
    }

    @Override
    public void show(RealTimeDataFormat format) {
        this.format = format;
        handler.sendEmptyMessage(msgShowRealTime);
    }
}
