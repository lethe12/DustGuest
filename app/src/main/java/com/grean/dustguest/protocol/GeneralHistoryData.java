package com.grean.dustguest.protocol;

import java.util.ArrayList;

/**
 * Created by weifeng on 2018/1/24.
 */

public class GeneralHistoryData {
    private ArrayList<GeneralMinData> list = new ArrayList<>();

    public GeneralHistoryData(){

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
