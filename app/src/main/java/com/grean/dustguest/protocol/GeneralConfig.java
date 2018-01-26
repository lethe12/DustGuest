package com.grean.dustguest.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weifeng on 2018/1/25.
 */

public class GeneralConfig {
    private String[] dustNames,clientProtocolNames;
    private int dustName,dustMeterPumpTime,dustMeterLaserTime,serverPort,clientProtocolName,MotorTime,MotorStep;
    private float dustParaK,alarmDust;
    private boolean autoCalEnable;
    private long autoCalTime,autoCalInterval;
    private String serverIp,mnCode,configContent,dustNameContent,dustMeterInfoContent,devicesId;

    public GeneralConfig(){

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        configContent = jsonObject.toString();
    }

    public void setDustNameContent(JSONObject jsonObject) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dustNameContent = jsonObject.toString();
    }

    public String[] getDustNames() {
        return dustNames;
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
