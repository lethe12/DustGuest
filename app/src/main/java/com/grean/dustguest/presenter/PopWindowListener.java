package com.grean.dustguest.presenter;

import android.content.Intent;

/**
 * Created by weifeng on 2018/1/21.
 */

public interface PopWindowListener {
    void OnInputIdComplete(String string);
    String[] getLastIdList();

}
