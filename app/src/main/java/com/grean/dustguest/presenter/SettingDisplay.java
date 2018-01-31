package com.grean.dustguest.presenter;

import com.grean.dustguest.protocol.GeneralConfig;

/**
 * Created by weifeng on 2018/1/29.
 */

public interface SettingDisplay {
    void show(GeneralConfig config);
    void cancelDialogWithToast(String string);
    void cancelDialog();
}
