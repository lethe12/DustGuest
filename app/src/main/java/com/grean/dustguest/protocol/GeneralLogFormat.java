package com.grean.dustguest.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weifeng on 2018/2/2.
 */

public class GeneralLogFormat {
    private List<Long> date = new ArrayList<>();
    private List<String> content = new ArrayList<>();

    public List<Long> getDate() {
        return date;
    }

    public List<String> getContent() {
        return content;
    }

    public int getSize(){
        return date.size();
    }

    public void clear(){
        date.clear();
        content.clear();
    }

    public void addOneItem(long time,String content){
        date.add(time);
        this.content.add(content);
    }
}
