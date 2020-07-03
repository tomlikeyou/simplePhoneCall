package com.hj.call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public String[] phoneStates = {"正在拨打", "通话中", "未在通话"};
    private TextView textView;
    private EditText editText;
    private PhoneStateListener phoneStateListener;
    private Button callOutBtn;
    private Button callCancelBtn;
    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化TelephonyManager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //获取控件
        editText = findViewById(R.id.PhoneEdit);
        textView = findViewById(R.id.callStatus);
        callOutBtn = findViewById(R.id.callOutBtn);
        callCancelBtn = findViewById(R.id.callCancelBtn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化电话状态监听器
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                switch (state) {
                    //待机，即无电话时候，挂断时候触发
                    case TelephonyManager.CALL_STATE_IDLE:
                        textView.setText("未在通话");
                        callCancelBtn.setEnabled(false);
                        break;
                    //响铃，来电时候触发
                    case TelephonyManager.CALL_STATE_RINGING:
                        textView.setText("正在拨打");
                        break;
                    //摘机，接听或拨出电话时触发
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        textView.setText("正在通话中");
                        //设置按钮不可点击
                        callOutBtn.setEnabled(false);
                        break;
                }
            }
        };
        //监听电话通话状态的改变
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        callOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查是否获得了权限（Android6.0运行时权限）
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.CALL_PHONE)) {
                        // 返回值：
                        //如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
                        //如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
                        //如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                        // 弹窗需要解释为何需要该权限，再次请求授权
                        Toast.makeText(MainActivity.this, "请授权！", Toast.LENGTH_LONG).show();
                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                1);
                    }
                } else {
                    //拨打电话
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    String phone = editText.getText().toString();
                    Log.d(TAG, "即将拨打的电话信息" + phone);
                    Uri data = Uri.parse("tel:" + phone);
                    intent.setData(data);
                    startActivity(intent);
                }
            }
        });

        callCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }
}