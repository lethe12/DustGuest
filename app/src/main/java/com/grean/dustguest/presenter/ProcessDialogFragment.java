package com.grean.dustguest.presenter;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grean.dustguest.R;

/**
 * Created by weifeng on 2017/9/13.
 */

public class ProcessDialogFragment extends DialogFragment implements NotifyProcessDialogInfo{
    private TextView tvInfo;
    private String string;
    private DialogDestroyListener destroyListener;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if((msg.what==1)&&(string!=null)){
                tvInfo.setText(string);
            }
        }
    };

    @Override
    public void onDestroy() {
        Log.d("ProcessDialogFragment","cancel Init Dialog");
        if(destroyListener!=null){
            destroyListener.onComplete();
        }
        super.onDestroy();
    }

    public void setDestroyListener (DialogDestroyListener listener){
        this.destroyListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN);
        getDialog().setTitle("操作中");
        View view = inflater.inflate(R.layout.fragment_process_dialog,container);
        tvInfo = view.findViewById(R.id.tvProcessDialog);

        return view;
    }

    @Override
    public void showInfo(String string) {
        this.string = string;
        handler.sendEmptyMessage(1);
    }
}
