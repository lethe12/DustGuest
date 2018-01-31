package com.grean.dustguest.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
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
import android.widget.Toast;

import com.grean.dustguest.R;
import com.grean.dustguest.model.SettingManager;
import com.grean.dustguest.protocol.GeneralConfig;
import com.grean.dustguest.protocol.ProtocolLib;
import com.grean.dustguest.protocol.RealTimeDataFormat;
import com.tools;

import java.util.Calendar;

/**
 * Created by weifeng on 2018/1/22.
 */

public class SettingActivity extends Activity implements View.OnClickListener,AdapterView.OnItemSelectedListener,
        SettingManagerListener,RealTimeSettingDisplay,SettingDisplay,DialogTimeSelected{
    private static final String tag = "SettingActivity";
    private EditText etDustParaK,etAutoInterval,etMotorStep,etMotorTime,etAlarmValue,etMnCode,etServerIp,etServerPort,etPassword,etUpdateUrl;
    private Switch swAutoCalEnable,swDustMeter,swRelay1,swRelay2,swRelay3,swRelay4,swRelay5;
    private TextView tvAutoCalTime,tvDustMeterInfo,tvRealTimeState;
    private TextView[] tvRealTimeValue=new TextView[16];
    private Spinner spDustName,spProtocols;
    private LinearLayout motorSet1,MotorSet2,RelaySet,passwordSet,realTimeDisplay;
    private Button btnSaveDustPara,btnSaveAutoCal;
    private SettingManager manager;
    private int dustName,protocolName;
    private String toastString;
    private RealTimeDataFormat format;
    private GeneralConfig config;
    private ProcessDialogFragment dialogFragment;
    private static final int msgShowRealTime = 1,msgShowSetting = 2,msgDismissDialogWithToast=3,
    msgDismissDialog=4;

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
                case msgShowSetting:
                    if(config!=null){
                        showContent(config);
                    }
                    break;
                case msgDismissDialogWithToast:
                    if(dialogFragment!=null){
                        dialogFragment.dismiss();
                        Toast.makeText(SettingActivity.this,toastString,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case msgDismissDialog:
                    if(dialogFragment!=null){
                        dialogFragment.dismiss();
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        swRelay3 = findViewById(R.id.swRelay3);
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
        btnSaveAutoCal = findViewById(R.id.btnOperateSaveAutoCal);
        btnSaveAutoCal.setOnClickListener(this);
        findViewById(R.id.btnOperateSaveAlarm).setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.btnOperateDustSetParaK:
                manager.setDustParams(Float.valueOf( etDustParaK.getText().toString()));
                break;
            case R.id.btnOperateSaveAutoCal:
                manager.setAutoDate(tvAutoCalTime.getText().toString(),etAutoInterval.getText().toString());
                break;
            case R.id.swOperateAutoCal:
                boolean enable = swAutoCalEnable.isChecked();
                if(!enable){
                    tvAutoCalTime.setVisibility(View.INVISIBLE);
                    etAutoInterval.setVisibility(View.INVISIBLE);
                    btnSaveAutoCal.setVisibility(View.INVISIBLE);
                }else{
                    tvAutoCalTime.setVisibility(View.VISIBLE);
                    etAutoInterval.setVisibility(View.VISIBLE);
                    btnSaveAutoCal.setVisibility(View.VISIBLE);
                }
                manager.setAutoCalEnable(enable);
                break;
            case R.id.btnSaveMotorParams:
                manager.setMotorParams(etMotorTime.getText().toString(),etMotorStep.getText().toString());
                break;
            case R.id.btnOperateSaveAlarm:
                manager.setAlarmValue(etAlarmValue.getText().toString());
                break;
            case R.id.btnOperateSaveServer:
                manager.setProtocol(etMnCode.getText().toString(),protocolName,
                        etServerIp.getText().toString(),Integer.valueOf(etServerPort.getText().toString()));
                break;
            case R.id.btnSaveDustType:
                manager.setDustName(dustName);
                break;
            case R.id.btnOperateUpdateSetting:
                manager.updateSetting(this);
                break;
            case R.id.tvOperateAutoCalDate:
                Calendar calendar = Calendar.getInstance();
                DialogTimeChoose choose = new DialogTimeChoose(this,"设置下次自动校准时间");
                choose.showDialog(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,this);
                break;
            case R.id.btnOperateCalMan:
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.show(getFragmentManager(),"Calibration");
                manager.startDustMeterCal(dialogFragment,this);
                break;
            case R.id.btnOperateUpdateSoftware:
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(true);
                dialogFragment.show(getFragmentManager(),"DownLoadSoftware");
                manager.startDownLoadSoftware(this,etUpdateUrl.getText().toString(),dialogFragment,this);
                break;
            case R.id.btnOperateVideoSetting:
                Uri uri = Uri.parse("http://192.168.1.64");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                break;
            case R.id.swDustMeter:
                manager.ctrlDustMeter(swDustMeter.isChecked());
                break;
            case R.id.swRelay1:
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(true);
                dialogFragment.show(getFragmentManager(),"CtrlRelay");
                manager.ctrlRelay(1,swRelay1.isChecked(),this);
                break;
            case R.id.swRelay2:
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(true);
                dialogFragment.show(getFragmentManager(),"CtrlRelay");
                manager.ctrlRelay(2,swRelay2.isChecked(),this);
                break;
            case R.id.swRelay3:
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(true);
                dialogFragment.show(getFragmentManager(),"CtrlRelay");
                manager.ctrlRelay(3,swRelay3.isChecked(),this);
                break;
            case R.id.swRelay4:
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(true);
                dialogFragment.show(getFragmentManager(),"CtrlRelay");
                manager.ctrlRelay(4,swRelay4.isChecked(),this);
                break;
            case R.id.swRelay5:
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(true);
                dialogFragment.show(getFragmentManager(),"CtrlRelay");
                manager.ctrlRelay(5,swRelay5.isChecked(),this);
                break;
            case R.id.btnForwardTest:
                manager.ctrlMotorForwardTest();
                break;
            case R.id.btnBackwardTest:
                manager.ctrlMotorBackwardTest();
                break;
            case R.id.btnForwardStep:
                manager.ctrlMotorForwardStep();
                break;
            case R.id.btnBackwardStep:
                manager.ctrlMotorBackwardStep();
                break;
            default:

                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()){
                case R.id.spDustType:
                    Log.d(tag,String.valueOf(position));
                    dustName = position;
                    break;
                case R.id.spOperateProticol:
                    protocolName = position;
                    break;
                default:
                    break;
            }
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
            btnSaveAutoCal.setVisibility(View.INVISIBLE);
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

    @Override
    public void show(GeneralConfig config) {
        this.config = config;
        handler.sendEmptyMessage(msgShowSetting);
    }

    @Override
    public void cancelDialogWithToast(String string) {
        toastString = string;
        handler.sendEmptyMessage(msgDismissDialogWithToast);
    }

    @Override
    public void cancelDialog() {
        Log.d(tag,"cancelDialog");
        handler.sendEmptyMessage(msgDismissDialog);
    }

    @Override
    public void onComplete(String string) {
        tvAutoCalTime.setText(manager.calcNextDate(string,etAutoInterval.getText().toString()));
    }
}
