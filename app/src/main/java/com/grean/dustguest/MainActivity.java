package com.grean.dustguest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.utils.CommonUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String tag = "MainActivity";
    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    //@BindView(R.id.btnTestScan)private Button btnTestScan;
    private Button btnTestScan;
    private TextView tvScanResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTestScan = findViewById(R.id.btnTestScan);
        tvScanResult = findViewById(R.id.tvScanResult);
        btnTestScan.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(tag,"回调");
        super.onActivityResult(requestCode, resultCode, data);
//扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("qr_scan_result");
            //将扫描出的信息显示出来
            Log.d(tag,scanResult);
            tvScanResult.setText(scanResult);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnTestScan:
                Log.d(tag,"开始扫描");

                if(CommonUtil.isCameraCanUse()){
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }else{
                    Toast.makeText(this,"摄像头权限",Toast.LENGTH_LONG).show();
                }
                break;
        }

    }
}
