package com.zhpan.ovallockview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zhpan.ovallockview.listener.OnLockOperateListener;
import com.zhpan.ovallockview.view.LockView;

public class MainActivity extends AppCompatActivity {
    private LockView mLockView;
    private static Handler mHandler = new Handler();
    private Runnable mLockRunnable = new Runnable() {
        @Override
        public void run() {
            mLockView.stopWave();
            mLockView.changeLockState(true);
            mLockView.setText("已上锁");
        }
    };

    private Runnable mUnlockRunnable = new Runnable() {
        @Override
        public void run() {
            mLockView.stopWave();
            mLockView.changeLockState(false);
            mLockView.setText("未上锁");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLockView = findViewById(R.id.lock_view);
        mLockView.setBluetoothConnect(false);
        mLockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLockView.connecting(true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLockView.connecting(false);
                        mLockView.setBluetoothConnect(true);
                    }
                },2000);
//                Toast.makeText(MainActivity.this, "点击了", Toast.LENGTH_SHORT).show();
            }
        });

        if(mLockView.isLock()){
            mLockView.setText("已上锁");
        }else {
            mLockView.setText("未上锁");
        }
        mLockView.setOnLockOperateListener(new OnLockOperateListener() {
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
                Message message = Message.obtain();
                message.what=1;
                mHandler.sendMessage(message);
                mHandler.postDelayed(mLockRunnable, 3000);
            }

            @Override
            public void onUnlockStart() {
                mLockView.startWave();
                mLockView.setText("正在开锁");
                Message message = Message.obtain();
                message.what=1;
                mHandler.sendMessage(message);
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
        });
    }
}
