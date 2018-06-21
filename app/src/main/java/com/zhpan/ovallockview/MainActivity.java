package com.zhpan.ovallockview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zhpan.lockview.listener.OnLockOperateListener;
import com.zhpan.lockview.view.LockView;

public class MainActivity extends AppCompatActivity implements OnLockOperateListener, View.OnClickListener {
    private LockView mLockView;
    private static Handler mHandler = new Handler();
    private Runnable mLockRunnable = new Runnable() {
        @Override
        public void run() {
            changeLockState(true);
        }
    };

    private Runnable mUnlockRunnable = new Runnable() {
        @Override
        public void run() {
            changeLockState(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLockView = findViewById(R.id.lock_view);
        mLockView.setBluetoothConnect(false);
        mLockView.setOnLockOperateListener(this);
        mLockView.setOnClickListener(this);
        if (mLockView.isLock()) {
            mLockView.setText("已上锁");
        } else {
            mLockView.setText("未上锁");
        }
    }

    private void changeLockState(boolean isLock) {
        mLockView.stopWave();
        mLockView.setLockState(isLock);
        if (isLock)
            mLockView.setText("已上锁");
        else
            mLockView.setText("未上锁");
    }

    @Override
    public void onLockPrepared() {
        mLockView.setText("释放上锁");
    }

    @Override
    public void onUnLockPrepared() {
        mLockView.setText("释放开锁");
    }

    @Override
    public void onLockStart() {
        mLockView.setText("正在上锁");
        mLockView.startWave();
        mHandler.postDelayed(mLockRunnable, 3000);
    }

    @Override
    public void onUnlockStart() {
        mLockView.startWave();
        mLockView.setText("正在开锁");
        mHandler.postDelayed(mUnlockRunnable, 3000);
    }

    @Override
    public void onNotPrepared() {
        if (mLockView.isLock()) {
            mLockView.setText("已上锁");
        } else {
            mLockView.setText("未上锁");
        }
    }

    @Override
    public void onClick(View v) {
        mLockView.connecting(true);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLockView.connecting(false);
                mLockView.setBluetoothConnect(true);
            }
        }, 2000);
    }
}
