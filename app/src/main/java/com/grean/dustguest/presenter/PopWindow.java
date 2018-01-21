package com.grean.dustguest.presenter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.zxing.StartScanForResult;
import com.grean.dustguest.R;

/**
 * Created by weifeng on 2018/1/21.
 */

public class PopWindow extends PopupWindow implements View.OnClickListener{
    private View conentView;
    private Context mContext;
    private PopWindowListener listener;

    private StartScanForResult startScanForResult;
    public PopWindow(final Activity context,StartScanForResult startScanForResult,PopWindowListener listener){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_window, null);
        this.mContext = context;
        this.startScanForResult = startScanForResult;
        this.listener = listener;
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 2 + 40);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        conentView.findViewById(R.id.tvScanId).setOnClickListener(this);
        conentView.findViewById(R.id.tvInputID).setOnClickListener(this);
        conentView.findViewById(R.id.tvLastId).setOnClickListener(this);
        conentView.findViewById(R.id.tvSearchData).setOnClickListener(this);
        conentView.findViewById(R.id.tvSearchLog).setOnClickListener(this);
        conentView.findViewById(R.id.tvAdvanceSetting).setOnClickListener(this);
        conentView.findViewById(R.id.about).setOnClickListener(this);
        conentView.findViewById(R.id.settings).setOnClickListener(this);
        conentView.findViewById(R.id.ability_logout).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // do something before signing out
                context.finish();//退出
                PopWindow.this.dismiss();
            }
        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvScanId:
                if(startScanForResult!=null) {
                    startScanForResult.startScan();
                }
                break;
            case R.id.tvInputID:
                final EditText et = new EditText(mContext);
                new AlertDialog.Builder(mContext).setTitle("请输入设备ID").setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.OnInputIdComplete(et.getText().toString());
                            }
                        }).setNegativeButton("取消",null).show();
                break;
            case R.id.tvLastId:
                AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("请选择需要连接的设备").
                        setSingleChoiceItems(listener.getLastIdList(), -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.OnInputIdComplete(listener.getLastIdList()[which]);
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.tvSearchData:

                break;
            case R.id.tvSearchLog:

                break;
            case R.id.settings:

                break;
            case R.id.tvAdvanceSetting:


                break;
            case R.id.about:

                break;
            default:

                break;
        }
        PopWindow.this.dismiss();
    }
}
