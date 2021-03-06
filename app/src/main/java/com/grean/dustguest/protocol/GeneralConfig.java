package com.grean.dustguest.protocol;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weifeng on 2018/1/25.
 */

public class GeneralConfig {
    private String[] dustNames,clientProtocolNames,dustMeterNames;
    private int dustMeter,dustName,dustMeterPumpTime,dustMeterLaserTime,serverPort,
            clientProtocolName,MotorTime,MotorStep,cameraOffset;
    private float dustParaK,dustParaB,alarmDust;
    private boolean autoCalEnable,cameraEnable;
    private long autoCalTime,autoCalInterval;
    private String serverIp,mnCode,configContent,dustNameContent,dustMeterInfoContent,devicesId;

    public GeneralConfig(){

    }

    public int getCameraOffset() {
        return cameraOffset;
    }

    public void setCameraOffset(int cameraOffset) {
        this.cameraOffset = cameraOffset;
    }

    public boolean isCameraEnable() {
        return cameraEnable;
    }

    public void setCameraEnable(boolean cameraEnable) {
        this.cameraEnable = cameraEnable;
    }

    public int getDustMeter() {
        return dustMeter;
    }

    public void setDustName(int dustName) {
        this.dustName = dustName;
    }

    public void setDustMeter(int dustMeter) {
        this.dustMeter = dustMeter;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setClientProtocolName(int clientProtocolName) {
        this.clientProtocolName = clientProtocolName;
    }

    public void setMotorTime(int motorTime) {
        MotorTime = motorTime;
    }

    public void setMotorStep(int motorStep) {
        MotorStep = motorStep;
    }

    public void setDustParaK(float dustParaK) {
        this.dustParaK = dustParaK;
    }

    public float getDustParaB() {
        return dustParaB;
    }

    public void setDustParaB(float dustParaB) {
        this.dustParaB = dustParaB;
    }

    public void setAlarmDust(float alarmDust) {
        this.alarmDust = alarmDust;
    }

    public void setAutoCalEnable(boolean autoCalEnable) {
        this.autoCalEnable = autoCalEnable;
    }

    public void setAutoCalTime(long autoCalTime) {
        this.autoCalTime = autoCalTime;
    }

    public void setAutoCalInterval(long autoCalInterval) {
        this.autoCalInterval = autoCalInterval;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setMnCode(String mnCode) {
        this.mnCode = mnCode;
    }

    public String getDevicesId() {
        return devicesId;
    }

    public void setDevicesId(String devicesId) {
        this.devicesId = devicesId;
    }

    public String getConfigContent() {
        return configContent;
    }

    public String getDustNameContent() {
        return dustNameContent;
    }

    public String getDustMeterInfoContent() {
        return dustMeterInfoContent;
    }

    public void setDustMeterInfoContent(JSONObject jsonObject) {
        try {
            dustMeterPumpTime = jsonObject.getInt("DustMeterPumpTime");
            dustMeterLaserTime = jsonObject.getInt("DustMeterLaserTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dustMeterInfoContent = jsonObject.toString();
    }

    public void setConfigContent(JSONObject jsonObject) {
        try {
            autoCalEnable = jsonObject.getBoolean("autoCalEnable");
            autoCalTime = jsonObject.getLong("autoCalTime");
            autoCalInterval = jsonObject.getLong("autoCalInterval");
            serverIp = jsonObject.getString("serverIp");
            serverPort = jsonObject.getInt("serverPort");
            mnCode = jsonObject.getString("mnCode");
            dustParaK = (float) jsonObject.getDouble("dustParaK");
            if(jsonObject.has("dustParaB")) {
                dustParaB = (float) jsonObject.getDouble("dustParaB");
            }
            alarmDust = (float) jsonObject.getDouble("alarmDust");
            clientProtocolName = jsonObject.getInt("clientProtocolName");
            JSONArray array = jsonObject.getJSONArray("clientProtocolNames");
            int size = array.length();
            if(size!=0) {
                clientProtocolNames = new String[size];
                for (int i = 0; i < size; i++) {
                    clientProtocolNames[i] = array.getString(i);
                }
            }

            if(jsonObject.has("motorTime")){
                MotorTime = jsonObject.getInt("motorTime");
            }

            if(jsonObject.has("motorStep")){
                MotorStep = jsonObject.getInt("motorStep");
            }

            if(jsonObject.has("cameraEnable")){
                cameraEnable = jsonObject.getBoolean("cameraEnable");
            }

            if(jsonObject.has("cameraOffset")){
                cameraOffset = jsonObject.getInt("cameraOffset");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        configContent = jsonObject.toString();
    }



    public void setDustNameContent(JSONObject jsonObject) {
        //Log.d("config","粉尘仪");
        try {
            JSONArray array = jsonObject.getJSONArray("dustNames");
            int size = array.length();
            if(size!=0){
                dustNames = new String[size];
                for(int i=0;i<size;i++){
                    dustNames[i] = array.getString(i);
                }
                dustName = jsonObject.getInt("dustName");
            }else{
                dustNames = new String[]{"TSP"};
                dustName = 0;
            }
            Log.d("config","配置粉尘仪");
            if(jsonObject.has("dustMeterNames")){
                array = jsonObject.getJSONArray("dustMeterNames");

                if(array.length() >0) {
                    dustMeterNames = new String[array.length()];
                    for (int i = 0; i < dustMeterNames.length; i++) {
                        dustMeterNames[i] = array.getString(i);

                        Log.d("config",dustMeterNames[i]);
                    }
                }else{
                    dustMeterNames = new String[] {"default"};
                }
            }

            if(jsonObject.has("dustMeter")){
                dustMeter = jsonObject.getInt("dustMeter");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        dustNameContent = jsonObject.toString();
    }

    public String[] getDustNames() {
        return dustNames;
    }

    public String[] getDustMeterNames() {
        return dustMeterNames;
    }

    public String[] getClientProtocolNames() {
        return clientProtocolNames;
    }

    public int getDustName() {
        return dustName;
    }

    public int getDustMeterPumpTime() {
        return dustMeterPumpTime;
    }

    public int getDustMeterLaserTime() {
        return dustMeterLaserTime;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getClientProtocolName() {
        return clientProtocolName;
    }

    public float getDustParaK() {
        return dustParaK;
    }

    public float getAlarmDust() {
        return alarmDust;
    }

    public boolean isAutoCalEnable() {
        return autoCalEnable;
    }

    public long getAutoCalTime() {
        return autoCalTime;
    }

    public long getAutoCalInterval() {
        return autoCalInterval;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getMnCode() {
        return mnCode;
    }

    public int getMotorTime() {
        return MotorTime;
    }

    public int getMotorStep() {
        return MotorStep;
    }
}
