package com.grean.dustguest.protocol;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.grean.dustguest.model.DustMeterCalProcessFormat;
import com.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weifeng on 2018/1/23.
 */

public class JSON {
    private static final String tag="JSON";

    public static boolean isFrameRight(String content){
        if(content.length() <12){
            return false;
        }

        if(!content.substring(0,2).equals("##")){
            return false;
        }

        if(!content.substring(content.length()-2,content.length()).equals("\r\n")){
            return false;
        }

        String string = content.substring(2,content.indexOf("$$"));
        try{
            int len = Integer.valueOf(string);
            if(len!=(content.length()-12)){
                return false;
            }
        }catch (NumberFormatException e){
            return false;
        }

        return true;
    }

    private static byte[] insertFrame(String content){
        String lenString = String.format("%06d",content.length());
        return ("##"+lenString+"$$"+content+"\r\n").getBytes();
    }

    public static byte[] readRealTimeData() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","realTimeData");
        return insertFrame(object.toString());
    }

    public static byte[] readOperateInit() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operateInit");
        return insertFrame(object.toString());
    }

    public static RealTimeDataFormat getRealTimeData(JSONObject jsonObject) throws JSONException {
        RealTimeDataFormat format = new RealTimeDataFormat();
        format.setState(jsonObject.getString("state"));

        if(jsonObject.has("alarm")) {
            format.setAlarm(jsonObject.getBoolean("alarm"));
        }
        if(jsonObject.has("serverConnected")){
            format.setServerConnected(jsonObject.getBoolean("serverConnected"));
        }
        if(jsonObject.has("acOk")){
            format.setAcOk(jsonObject.getBoolean("acOk"));
        }
        if(jsonObject.has("batteryLow")){
            format.setAcOk(jsonObject.getBoolean("batteryLow"));
        }
        if(jsonObject.has("heatPwm")){
            format.setHeatParams(jsonObject.getInt("heatPwm"));
        }

        if(jsonObject.has("calPos")){
            format.setCalPos(jsonObject.getBoolean("calPos"));
        }

        if(jsonObject.has("measurePos")){
            format.setMeasurePos(jsonObject.getBoolean("measurePos"));
        }

        format.setRelays(1,jsonObject.getBoolean("relay1"));
        format.setRelays(2,jsonObject.getBoolean("relay2"));
        format.setRelays(3,jsonObject.getBoolean("relay3"));
        format.setRelays(4,jsonObject.getBoolean("relay4"));
        format.setRelays(5,jsonObject.getBoolean("relay5"));
        format.setDustMeterRun(jsonObject.getBoolean("dustMeterRun"));
        JSONArray array = jsonObject.getJSONArray("realTimeData");
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
            }else if(item.getString("name").equals("highDew")){
                format.setEntranceDewPoint((float) item.getDouble("value"));
            }else if(item.getString("name").equals("lowDew")){
                format.setExitDewPoint((float) item.getDouble("value"));
            }else if(item.getString("name").equals("heatParams")){
                format.setHeatParams((float) item.getDouble("value"));
            }else if(item.getString("name").equals("exitHumidity")){
                format.setExitHumidity((float) item.getDouble("value"));
            }else if(item.getString("name").equals("exitTemperature")){
                format.setExitTemperature((float) item.getDouble("value"));
            }else if(item.get("name").equals("pipeTemperature")) {
                format.setPipeTemperature((float) item.getDouble("value"));
            }else if(item.getString("name").equals("targetTemperature")){
                format.setTargetTemperature((float) item.getDouble("value"));
            }else{

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

    public static byte[] operateNoiseCalibration() throws JSONException{
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("NoiseCalibration",true);
        return insertFrame(object.toString());
    }

    public static byte[] getNoiseCalibrationState() throws JSONException{
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("NoiseCalibrationState",true);
        return insertFrame(object.toString());
    }

    public static byte[] operateDustMeterCal() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("DustMeterCal",true);
        return insertFrame(object.toString());
    }

    public static byte[] operateDustMeterCalProcess() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("DustMeterCalProcess",true);
        return insertFrame(object.toString());
    }

    public static byte[] operateDustMeterCalResult() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("DustMeterCalResult",true);
        return insertFrame(object.toString());
    }

    public static String getDustMeterCalResult(JSONObject jsonObject) throws JSONException {
        boolean bg = jsonObject.getBoolean("DustMeterCalBg");
        boolean span = jsonObject.getBoolean("DustMeterCalSpan");
        String string;
        if(bg){
            string = "校零成功，";
        }else{
            string = "校零失败，";
        }
        if(span){
            string += "校跨成功。";
        }else{
            string += "校跨失败。";
        }
        return string;
    }

    public static DustMeterCalProcessFormat getDustMeterCalProcess(JSONObject jsonObject) throws JSONException {
        DustMeterCalProcessFormat format = new DustMeterCalProcessFormat();
        String string = jsonObject.getString("DustMeterCalInfo");
        int process = jsonObject.getInt("DustMeterCalProcessInt");
        string += "..."+String.valueOf(process)+"%";
        format.setProcess(process);
        format.setString(string);
        return format;
    }

    public static byte[] uploadConfig(GeneralConfig config) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","uploadSetting");
        object.put("autoCalEnable",config.isAutoCalEnable());
        object.put("autoCalTime",config.getAutoCalTime());
        object.put("autoCalInterval",config.getAutoCalInterval());
        object.put("serverIp",config.getServerIp());
        object.put("serverPort",config.getServerPort());
        object.put("mnCode",config.getMnCode());
        object.put("alarmDust",config.getAlarmDust());
        object.put("clientProtocolName",config.getClientProtocolName());
        object.put("motorTime",config.getMotorTime());
        object.put("motorStep",config.getMotorStep());
        object.put("dustName",config.getDustName());
        object.put("dustMeter",config.getDustMeter());
        return insertFrame(object.toString());
    }

    public static void getDustName(JSONObject jsonObject,GeneralConfig config) throws JSONException {
        config.setDustNameContent(jsonObject);
    }

    public static byte[] operateDustSetParaK(float k,float b) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("DustMeterSetParaK",true);
        object.put("DustMeterParaK",k);
        object.put("DustMeterParaB",b);
        return insertFrame(object.toString());
    }

    public static byte[] readHistoryData(long startDate,long endDate) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","historyData");
        object.put("startDate",startDate);
        object.put("endDate",endDate);
        return insertFrame(object.toString());
    }

    public static byte[] readHistoryHourData(long startDate,long endDate) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","historyHourData");
        object.put("startDate",startDate);
        object.put("endDate",endDate);
        return insertFrame(object.toString());
    }

    /**
     * 请求日志信息
     * @param startDate 查询的起始时间
     * @param endDate 查询的截止时间
     * @return
     * @throws JSONException
     */
    public static byte[] readLog(long startDate,long endDate) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","log");
        object.put("startDate",startDate);
        object.put("endDate",endDate);
        return insertFrame(object.toString());
    }

    public static byte[] readSetting() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","downloadSetting");
        return insertFrame(object.toString());
    }

    public static byte[] readDustMeterInfo() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("DustMeterInfo",true);
        return insertFrame(object.toString());
    }

    /**
     * 控制粉尘仪，开启或关闭
     * @param key
     * @return
     * @throws JSONException
     */
    public static byte[] ctrlDustMeter(boolean key) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("DustMeterRun",key);
        return insertFrame(object.toString());
    }

    /**
     * 控制继电器
     * @param num
     * @param key
     * @return
     * @throws JSONException
     */
    public static byte[] ctrlRelay(int num,boolean key) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("RelayCtrl",true);
        object.put("num",num);
        object.put("key",key);
        return insertFrame(object.toString());
    }

    /**
     * 正转测试
     * @return
     * @throws JSONException
     */
    public static byte[] ctrlMotorForwardTest() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("MotorForwardTest",true);
        return insertFrame(object.toString());
    }

    /**
     * 反转测试
     * @return
     * @throws JSONException
     */
    public static byte[] ctrlMotorBackwardTest() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("MotorBackwardTest",true);
        return insertFrame(object.toString());
    }

    /**
     * 正转100步
     * @return
     * @throws JSONException
     */
    public static byte[] ctrlMotorForwardStep() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("MotorForwardStep",true);
        return insertFrame(object.toString());
    }

    /**
     * 反转100步
     * @return
     * @throws JSONException
     */
    public static byte[] ctrlMotorBackwardStep() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("protocolType","operate");
        object.put("MotorBackwardStep",true);
        return insertFrame(object.toString());
    }

    public static void getDustMeterInfo(JSONObject jsonObject,GeneralConfig config) throws JSONException {
        config.setDustMeterInfoContent(jsonObject);
    }
    public static void getSetting(JSONObject jsonObject,GeneralConfig config) throws JSONException {
        config.setConfigContent(jsonObject);
    }

    public static void getLog(JSONObject jsonObject,GeneralLogFormat format) throws JSONException {
        if(jsonObject.has("ArrayData")){
            JSONArray array = jsonObject.getJSONArray("ArrayData");
            int size = array.length();
            for(int i=0; i <size ;i ++){
                format.addOneItem(i,array.getString(i));

            }
        }
    }

    /**
     * 获取历史数据
     * @param jsonObject
     * @param historyData
     * @throws JSONException
     */
    public static void getHistoryData(JSONObject jsonObject,GeneralHistoryData historyData) throws JSONException {
        //historyData.clear();
        int arraySize = jsonObject.getInt("DateSize");
        if(arraySize>0) {
            JSONArray array = jsonObject.getJSONArray("ArrayData");
            Log.d(tag, "json size=" + String.valueOf(arraySize) + ";array size = " + String.valueOf(array.length()));
            GeneralMinData minData;
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                minData = new GeneralMinData();
                minData.setDate(item.getLong("date"));
                minData.setDust((float) item.getDouble("dust"));
                minData.setTemperate((float) item.getDouble("temperature"));
                minData.setHumidity((float) item.getDouble("humidity"));
                minData.setPressure((float) item.getDouble("pressure"));
                minData.setWindForce((float) item.getDouble("windForce"));
                minData.setWindDirection((float) item.getDouble("windDirection"));
                minData.setNoise((float) item.getDouble("noise"));
                historyData.add(minData);
            }
        }
    }
}
