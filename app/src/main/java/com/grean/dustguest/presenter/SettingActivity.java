package com.grean.dustguest.presenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Layout;
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
import com.grean.dustguest.model.SystemConfig;
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
    private EditText etDustParaK,etDustParaB,etAutoInterval,etMotorStep,etMotorTime,etAlarmValue,
            etMnCode,etServerIp,etServerPort,etUpdateUrl,etCameraOffset;
    private Switch swAutoCalEnable,swDustMeter,swRelay1,swRelay2,swRelay3,swRelay4,
            swRelay5,swCameraFunction;
    private TextView tvAutoCalTime,tvDustMeterInfo,tvRealTimeState,tvDeviceId;
    private TextView[] tvRealTimeValue=new TextView[16];
    private Spinner spDustName,spProtocols,spDustMeter;
   /* private LinearLayout motorSet,motorTest,relaysSet,passwordSet,realTimeDisplay0,
            realTimeDisplay1,realTimeDisplay2,realTimeDisplay3,realTimeDisplay4,
            realTimeDisplay5,clearDevicesList,dustMeterInfo,softwareUpdate;*/
    //private ConstraintLayout dustNameList;
    private ConstraintLayout realTimeLayout,maintainingLayout;
    private Button btnSaveDustPara,btnSaveAutoCal;
    private SettingManager manager;
    private int dustName,protocolName,dustMeter;
    private String toastString;
    private RealTimeDataFormat format;
    private GeneralConfig config;
    private ProcessDialogFragment dialogFragment;
    private DialogProcessFragmentBarStyle dialogProcessFragmentBarStyle;
    private static final int msgShowRealTime = 1,msgShowSetting = 2,msgDismissDialogWithToast=3,
    msgDismissDialog=4,msgDismissDialogBarStyle =5;

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
                        //tvRealTimeValue[12].setText(tools.float2String3(format.getHeatParams()));
                        //tvRealTimeValue[13].setText(String.valueOf(format.isBatteryLow()));
                        //tvRealTimeValue[14].setText(String.valueOf(format.isAcOk()));
                        tvRealTimeValue[13].setText("限位:"+String.valueOf(format.isCalPos())+";测量:"+ String.valueOf(format.isMeasurePos()));
                        tvRealTimeValue[14].setText("电池:"+String.valueOf(format.isBatteryLow())+";外接:"+ String.valueOf(format.isAcOk()));
                        tvRealTimeValue[15].setText("加热管温度:"+String.valueOf(format.getPipeTemperature())+";预设温度:"
                                +String.valueOf(format.getTargetTemperature())+";加热系数:"+String.valueOf(format.getHeatParams()));
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
                case msgDismissDialogBarStyle:
                    if(dialogProcessFragmentBarStyle!=null){
                        dialogProcessFragmentBarStyle.dismiss();
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
        setContentView(R.layout.activity_setting_advance);
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
            etDustParaB.setEnabled(true);
            btnSaveDustPara.setEnabled(true);
        }else{
            etDustParaK.setEnabled(false);
            etDustParaB.setEnabled(false);
            btnSaveDustPara.setEnabled(false);
            realTimeLayout.setVisibility(View.GONE);
            maintainingLayout.setVisibility(View.GONE);
            /*realTimeDisplay0.setVisibility(View.GONE);
            motorSet.setVisibility(View.GONE);
            motorTest.setVisibility(View.GONE);
            relaysSet.setVisibility(View.GONE);
            passwordSet.setVisibility(View.GONE);
            realTimeDisplay1.setVisibility(View.GONE);
            realTimeDisplay2.setVisibility(View.GONE);
            realTimeDisplay3.setVisibility(View.GONE);
            realTimeDisplay4.setVisibility(View.GONE);
            realTimeDisplay5.setVisibility(View.GONE);
            clearDevicesList.setVisibility(View.GONE);
            dustMeterInfo.setVisibility(View.GONE);

            dustNameList.setVisibility(View.GONE);
            softwareUpdate.setVisibility(View.GONE);*/
        }

        if(getIntent().getBooleanExtra("online",true)){//在线状态
            tvDeviceId.setText("当前设备ID: "+getIntent().getStringExtra("id")+" 在线");
        }else {
            tvDeviceId.setText("当前设备ID: "+getIntent().getStringExtra("id")+" 离线");
        }

        manager = new SettingManager(this);
        manager.loadSetting();
        ProtocolLib.getInstance().getClientProtocol().setRealTimeSettingDisplay(this);
    }

    private void initView(){
        etDustParaK = findViewById(R.id.etOperateDustParaK);
        etDustParaB = findViewById(R.id.etOperateDustParaB);
        etAutoInterval = findViewById(R.id.etOperateAutoCalInterval);
        etMotorStep = findViewById(R.id.etMotorSetp);
        etMotorTime = findViewById(R.id.etMotorTime);
        etAlarmValue = findViewById(R.id.etOperateAlarm);
        etMnCode = findViewById(R.id.etOperateMnCode);
        etServerIp = findViewById(R.id.etOperateServerIp);
        etServerPort = findViewById(R.id.etOperateServerPort);
        etUpdateUrl = findViewById(R.id.etOperateUpdateSoftwareUrl);
        etCameraOffset = findViewById(R.id.etCameraOffset);
        swAutoCalEnable = findViewById(R.id.swOperateAutoCal);
        swDustMeter = findViewById(R.id.swDustMeter);
        swRelay1 = findViewById(R.id.swRelay1);
        swRelay2 = findViewById(R.id.swRelay2);
        swRelay3 = findViewById(R.id.swRelay3);
        swRelay4 = findViewById(R.id.swRelay4);
        swRelay5 = findViewById(R.id.swRelay5);
        spDustName = findViewById(R.id.spDustType);
        spDustMeter = findViewById(R.id.spDustMeter);
        spProtocols = findViewById(R.id.spOperateProticol);
        swCameraFunction = findViewById(R.id.swCameraFunction);
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
        /*realTimeDisplay0 = findViewById(R.id.layoutRealTimeDisplay0);
        realTimeDisplay1 = findViewById(R.id.layoutRealTimeDisplay1);
        realTimeDisplay2 = findViewById(R.id.layoutRealTimeDisplay2);
        realTimeDisplay3 = findViewById(R.id.layoutRealTimeDisplay3);
        realTimeDisplay4 = findViewById(R.id.layoutRealTimeDisplay4);
        realTimeDisplay5 = findViewById(R.id.layoutRealTimeDisplay5);
        motorSet = findViewById(R.id.layoutMotorSetting);
        motorTest = findViewById(R.id.layoutMotorTest);
        relaysSet = findViewById(R.id.layoutRelays);
        dustMeterInfo = findViewById(R.id.layoutOperateDustMeter);
        passwordSet = findViewById(R.id.layoutPassword);
        clearDevicesList = findViewById(R.id.layoutClearDevicesList);*/
        //dustNameList = findViewById(R.id.layoutDustName);
        realTimeLayout = findViewById(R.id.layoutRealTimeDisplay);
        maintainingLayout = findViewById(R.id.layoutMaintaining);

        //softwareUpdate = findViewById(R.id.layoutOperateUpdateSoftware);
        tvDeviceId = findViewById(R.id.tvDeviceId);
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
        findViewById(R.id.btnClearRecentDevices).setOnClickListener(this);
        findViewById(R.id.btnOperateRouterSetting).setOnClickListener(this);
        findViewById(R.id.btnOperateNoiseCalibration).setOnClickListener(this);
        findViewById(R.id.btnSaveCameraOffset).setOnClickListener(this);
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
        swCameraFunction.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOperateDustSetParaK:
                manager.setDustParams(Float.valueOf( etDustParaK.getText().toString()),Float.valueOf( etDustParaB.getText().toString()));
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
                manager.setDustName(dustName,dustMeter);
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
                dialogProcessFragmentBarStyle = new DialogProcessFragmentBarStyle();
                dialogProcessFragmentBarStyle.setCancelable(false);
                dialogProcessFragmentBarStyle.show(getFragmentManager(),"Calibration");
                /*dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(false);
                dialogFragment.show(getFragmentManager(),"Calibration");*/
                manager.startDustMeterCal(dialogProcessFragmentBarStyle,this);
                break;
            case R.id.btnOperateUpdateSoftware:
                dialogFragment = new ProcessDialogFragment();
                dialogFragment.setCancelable(true);
                dialogFragment.show(getFragmentManager(),"DownLoadSoftware");
                manager.startDownLoadSoftware(this,etUpdateUrl.getText().toString(),dialogFragment,this);
                break;
            case R.id.btnOperateVideoSetting:
                Uri uri = Uri.parse("http://192.168.1.64:80");
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
            case R.id.btnSavePassword:
                final EditText password = new EditText(this);
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                new AlertDialog.Builder(this).setTitle("请新输入密码").setView(password).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(password.getText().toString().isEmpty()){
                                    Toast.makeText(SettingActivity.this,"新密码不能为空",Toast.LENGTH_SHORT).show();
                                }else{
                                    manager.savePassword(SettingActivity.this,password.getText().toString());
                                    Toast.makeText(SettingActivity.this,"密码修改成功!",Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消",null).show();

                break;
            case R.id.btnClearRecentDevices:
                manager.clearRecentDevices(this);
                break;
            case R.id.btnOperateRouterSetting:
                Uri uriRouter = Uri.parse("http://192.168.1.1");
                Intent intentRouter = new Intent(Intent.ACTION_VIEW,uriRouter);
                startActivity(intentRouter);
                break;
            case R.id.btnOperateNoiseCalibration:
                dialogProcessFragmentBarStyle = new DialogProcessFragmentBarStyle();
                dialogProcessFragmentBarStyle.setCancelable(false);
                dialogProcessFragmentBarStyle.show(getFragmentManager(),"Noise Calibration");
                manager.startNoiseCalibration(dialogProcessFragmentBarStyle,this);
                break;
            case R.id.btnSaveCameraOffset:
                if(etCameraOffset.getText().toString() != null) {
                    if(!etCameraOffset.getText().toString().isEmpty()) {
                        int offset = Integer.valueOf(etCameraOffset.getText().toString());
                        if((offset < -359)||(offset > 359)){
                            Toast.makeText(SettingActivity.this,"偏离值,超范围!",Toast.LENGTH_SHORT).show();
                        }else{
                            manager.setCameraOffset(offset);
                            Toast.makeText(SettingActivity.this,"设置成功!",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SettingActivity.this,"设置失败,无效值!",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SettingActivity.this,"设置失败,无效值!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.swCameraFunction:
                manager.setCameraEnable(swCameraFunction.isChecked());
                Toast.makeText(SettingActivity.this,"设置成功,重启仪器生效!",Toast.LENGTH_SHORT).show();
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
                case R.id.spDustMeter:
                    dustMeter = position;
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
        etDustParaK.setText(String.valueOf(config.getDustParaK()));
        etDustParaB.setText(String.valueOf(config.getDustParaB()));
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

        ArrayAdapter<String>adapterDustMeter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,config.getDustMeterNames());
        spDustMeter.setAdapter(adapterDustMeter);
        dustMeter = config.getDustMeter();
        spDustMeter.setSelection(dustMeter);
        spDustMeter.setOnItemSelectedListener(this);

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
        etCameraOffset.setText(String.valueOf(config.getCameraOffset()));
        swCameraFunction.setChecked(config.isCameraEnable());
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
    public void cancelDialogBarStyleWithToast(String string) {
        toastString = string;
        handler.sendEmptyMessage(msgDismissDialogBarStyle);
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
