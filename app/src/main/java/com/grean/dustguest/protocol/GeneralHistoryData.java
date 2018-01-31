package com.grean.dustguest.protocol;

import com.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2018/1/24.
 */

public class GeneralHistoryData {
    private ArrayList<GeneralMinData> list = new ArrayList<>();

    public GeneralHistoryData(){

    }

    public List<String> getDate(){
        List<String> list = new ArrayList<>();
        GeneralMinData data;
        for (int i=0;i<this.list.size();i++){
            data = this.list.get(i);
            list.add(tools.timestamp2string(data.getDate()));
        }
        return list;
    }

    public List<List<String>> getData(){
        List<List<String>> lists = new ArrayList<>();
        List<String> element;// = new ArrayList<>();
        GeneralMinData data;
        for(int i=0;i<this.list.size();i++){
            data = this.list.get(i);
            element = new ArrayList<>();
            element.add(tools.float2String3(data.getDust()));
            element.add(tools.float2String1(data.getTemperate()));
            element.add(tools.float2String1(data.getHumidity()));
            element.add(tools.float2String0(data.getPressure()));
            element.add(tools.float2String1(data.getWindForce()));
            element.add(tools.float2String0(data.getWindDirection()));
            element.add(tools.float2String1(data.getNoise()));
            lists.add(element);
        }

        return lists;
    }


    public void add(GeneralMinData minData){
        list.add(minData);
    }

    public void clear(){
        list.clear();
    }

    public GeneralMinData get(int i){
        return list.get(i);
    }

    public int getSize(){
        return list.size();
    }
}
