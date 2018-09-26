package com.zhpan.ovallockview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhpan.lockview.listener.OnLockOperateListener;
import com.zhpan.lockview.view.LockView;


public class MainActivity extends AppCompatActivity implements OnLockOperateListener, View.OnClickListener {
    private LockView mLockView;
    private static Handler mHandler = new Handler();
    private Runnable mLockRunnable = new Runnable() {
        @Override
        public void run() {
            //  设置为上锁状态
            changeLockState(true);
        }
    };

    private Runnable mUnlockRunnable = new Runnable() {
        @Override
        public void run() {
            //  设置为未上锁状态
            changeLockState(false);

        }
    };
    private boolean isOperating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLockView = findViewById(R.id.lock_view);
        //  初始化时设置蓝牙未连接，此时可以点击，但不能滑动
        mLockView.setBluetoothConnect(false);
        //  添加滑动事件监听
        mLockView.setOnLockOperateListener(this);
        //  设置点击事件监听
        mLockView.setOnClickListener(this);
        //  设置滑动阻尼大小
        mLockView.setDamping(2);
        mLockView.setText("下滑开锁","上滑上锁");
        mLockView.setTextSize(14);
        mLockView.setCenterTextSize(18);
        mLockView.setConnectingText("正在连接");
//        mLockView.setDeviceFrozen("已冻结");
        //  设置上锁状态
     /*   if (mLockView.isLock()) {
            mLockView.setText("已上锁");
        } else {
            mLockView.setText("未上锁");
        }*/
        mLockView.setNoNetData(true,"未连接");
    }

    private void changeLockState(boolean isLock) {
        isOperating = false;
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
        if (isOperating) {
            Toast.makeText(this, "正在操作，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        isOperating = true;
        mLockView.setText("正在上锁");
        mLockView.startWave();
        mHandler.postDelayed(mLockRunnable, 3000);
    }

    @Override
    public void onUnlockStart() {
        if (isOperating) {
            Toast.makeText(this, "正在操作，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        isOperating = true;
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
                //  设置蓝牙已连接
//                mLockView.setBluetoothConnect(true);
                mLockView.setDeviceFrozen("已冻结");
            }
        }, 2000);
    }
}
