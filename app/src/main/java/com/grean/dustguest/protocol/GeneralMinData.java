package com.grean.dustguest.protocol;

/**
 * 存储分钟数据
 * Created by weifeng on 2018/1/24.
 */

public class GeneralMinData {
    private long date;
    private float dust,temperate,humidity,pressure,windForce,windDirection,noise,value;
    public GeneralMinData(){

    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public float getDust() {
        return dust;
    }

    public void setDust(float dust) {
        this.dust = dust;
    }

    public float getTemperate() {
        return temperate;
    }

    public void setTemperate(float temperate) {
        this.temperate = temperate;
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
