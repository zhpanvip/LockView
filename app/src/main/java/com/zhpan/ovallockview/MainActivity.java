package com.zhpan.ovallockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhpan.ovallockview.listener.OnLockOperateListener;
import com.zhpan.ovallockview.view.OvalLockView;

public class MainActivity extends AppCompatActivity {
    private OvalLockView mOvalLockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOvalLockView=findViewById(R.id.lock_view);
        mOvalLockView.setOnLockOperateListener(new OnLockOperateListener() {
            @Override
            public void onLockStart() {
                Toast.makeText(MainActivity.this, "正在上锁", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onUnlockStart() {
                Toast.makeText(MainActivity.this, "正在开锁", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
