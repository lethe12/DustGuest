package com.grean.dustguest.protocol;

/**
 * Created by weifeng on 2018/1/23.
 */

public class RealTimeDataFormat {
    private float dust,temperature,humidity,pressure,windForce,windDirection,noise,value,
            entranceDewPoint,exitDewPoint,heatParams,exitTemperature,exitHumidity,pipeTemperature,targetTemperature;
    private String state;
    boolean alarm,serverConnected,acOk,BatteryLow,dustMeterRun,calPos,measurePos;
    boolean [] relays = new boolean[5];

    public RealTimeDataFormat(){

    }

    public float getPipeTemperature() {
        return pipeTemperature;
    }

    public void setPipeTemperature(float pipeTemperature) {
        this.pipeTemperature = pipeTemperature;
    }

    public float getTargetTemperature() {
        return targetTemperature;
    }

    public void setTargetTemperature(float targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public boolean isCalPos() {
        return calPos;
    }

    public void setCalPos(boolean calPos) {
        this.calPos = calPos;
    }

    public boolean isMeasurePos() {
        return measurePos;
    }

    public void setMeasurePos(boolean measurePos) {
        this.measurePos = measurePos;
    }

    public boolean isDustMeterRun() {
        return dustMeterRun;
    }

    public void setDustMeterRun(boolean dustMeterRun) {
        this.dustMeterRun = dustMeterRun;
    }

    public boolean isAcOk() {
        return acOk;
    }

    public void setAcOk(boolean acOk) {
        this.acOk = acOk;
    }

    public boolean isBatteryLow() {
        return BatteryLow;
    }

    public void setBatteryLow(boolean batteryLow) {
        BatteryLow = batteryLow;
    }

    public boolean isServerConnected() {
        return serverConnected;
    }

    public void setServerConnected(boolean serverConnected) {
        this.serverConnected = serverConnected;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getDust() {
        return dust;
    }

    public float getEntranceDewPoint() {
        return entranceDewPoint;
    }

    public void setEntranceDewPoint(float entranceDewPoint) {
        this.entranceDewPoint = entranceDewPoint;
    }

    public float getExitDewPoint() {
        return exitDewPoint;
    }

    public void setExitDewPoint(float exitDewPoint) {
        this.exitDewPoint = exitDewPoint;
    }

    public float getHeatParams() {
        return heatParams;
    }

    public void setHeatParams(float heatParams) {
        this.heatParams = heatParams;
    }

    public float getExitTemperature() {
        return exitTemperature;
    }

    public void setExitTemperature(float exitTemperature) {
        this.exitTemperature = exitTemperature;
    }

    public float getExitHumidity() {
        return exitHumidity;
    }

    public void setExitHumidity(float exitHumidity) {
        this.exitHumidity = exitHumidity;
    }

    public void setDust(float dust) {
        this.dust = dust;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getWindForce() {
        return windForce;
    }

    public void setWindForce(float windForce) {
        this.windForce = windForce;
    }

    public float getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(float windDirection) {
        this.windDirection = windDirection;
    }

    public float getNoise() {
        return noise;
    }

    public void setNoise(float noise) {
        this.noise = noise;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean getRelays(int i) {
        if((i<=5)&&(i >0)) {
            return relays[i - 1];
        }else{
            return false;
        }
    }

    public void setRelays(int num,boolean key) {
        if((num<=5)&&(num >0)) {
            this.relays[num-1] = key;
        }
    }
}
