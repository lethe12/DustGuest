package com.grean.dustguest.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.grean.dustguest.R;

/**
 * Created by weifeng on 2018/1/22.
 */

public class SettingActivity extends Activity implements View.OnClickListener{
    private EditText etDustParaK,etAutoInterval,etMotorStep,etMotorTime,etAlarmValue,etMnCode,etServerIp,etServerPort,etPassword,etUpdateUrl;
    private Switch swAutoCalEnable,swDustMeter,swRelay1,swRelay2,swRelay3,swRelay4,swRelay5;
    private TextView tvAutoCalTime,tvDustMeterInfo,tvRealTimeState;
    private TextView[] tvRealTimeValue=new TextView[16];
    private Spinner spDustName,spProtocols;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        }else{

        }
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
        swRelay3 = findViewById(R.id.switch3);
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
        findViewById(R.id.btnOperateDustSetParaK).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

    }
}
