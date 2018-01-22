package com.grean.dustguest.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.grean.dustguest.R;

/**
 * Created by weifeng on 2018/1/22.
 */

public class SettingActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.setting_toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if(getIntent().getBooleanExtra("isAdvance",false)){//高级设置

        }else{

        }

    }
}
