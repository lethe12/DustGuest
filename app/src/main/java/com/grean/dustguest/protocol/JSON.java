package com.grean.dustguest.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weifeng on 2018/1/23.
 */

public class JSON {
    private static final String tag="JSON";
    public static byte[] readRealTimeData() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","realTimeData");
        return object.toString().getBytes();
    }

    public static byte[] readOperateInit() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operateInit");
        return object.toString().getBytes();
    }

    public static RealTimeDataFormat getRealTimeData(JSONObject jsonObject) throws JSONException {
        RealTimeDataFormat format = new RealTimeDataFormat();
        format.setState(jsonObject.getString("state"));
        JSONArray array = jsonObject.getJSONArray("realTimeData");
        if(jsonObject.has("alarm")) {
            format.setAlarm(jsonObject.getBoolean("alarm"));
        }
        if(jsonObject.has("serverConnected")){
            format.setServerConnected(jsonObject.getBoolean("serverConnected"));
        }
        for(int i=0; i <array.length();i++){
            JSONObject item = array.getJSONObject(i);
            if(item.getString("name").equals("dust")){
                format.setDust((float) item.getDouble("value"));
            }else if(item.getString("name").equals("temperature")){
                format.setTemperature((float) item.getDouble("value"));
            }else if(item.getString("name").equals("humidity")){
                format.setHumidity((float) item.getDouble("value"));
            }else if(item.getString("name").equals("pressure")){
                format.setPressure((float) item.getDouble("value"));
            }else if(item.getString("name").equals("windForce")){
                format.setWindForce((float) item.getDouble("value"));
            }else if(item.getString("name").equals("windDirection")){
                format.setWindDirection((float) item.getDouble("value"));
            }else if(item.getString("name").equals("noise")){
                format.setNoise((float) item.getDouble("value"));
            }else if(item.getString("name").equals("value")){
                format.setValue((float) item.getDouble("value"));
            }else if(item.getString("name").equals("entranceDewPoint")){
                format.setEntranceDewPoint((float) item.getDouble("value"));
            }else if(item.getString("name").equals("exitDewPoint")){
                format.setExitDewPoint((float) item.getDouble("value"));
            }else if(item.getString("name").equals("heatParams")){
                format.setHeatParams((float) item.getDouble("value"));
            }else if(item.getString("name").equals("exitHumidity")){
                format.setExitHumidity((float) item.getDouble("value"));
            }else if(item.getString("name").equals("exitTemperature")){
                format.setExitTemperature((float) item.getDouble("value"));
            }
        }
        return format;
    }

    public static String getProtocolType(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("protocolType")){
            return jsonObject.getString("protocolType");
        }else{
            return "";
        }
    }

    public static String getDustName(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.getJSONArray("dustNames");
        int size = array.length();
        if(size!=0){
            String[] names = new String[size];
            for(int i=0;i<size;i++){
                names[i] = array.getString(i);
            }
            int name = jsonObject.getInt("dustName");
            return names[name];
        }
        return "TSP";
    }
}
