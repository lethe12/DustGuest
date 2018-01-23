package com.grean.dustguest.protocol;

/**
 * Created by weifeng on 2018/1/23.
 */

public class RealTimeDataFormat {
    private float dust,temperature,humidity,pressure,windForce,windDirection,noise,value,
            entranceDewPoint,exitDewPoint,heatParams,exitTemperature,exitHumidity;
    private String state;
    boolean alarm,serverConnected;

    public RealTimeDataFormat(){

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
}
