package com.zhpan.ovallockview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zhpan.ovallockview.listener.OnLockOperateListener;
import com.zhpan.ovallockview.view.OvalLockView;

public class MainActivity extends AppCompatActivity {
    private OvalLockView mOvalLockView;
    private static Handler mHandler = new Handler();
    private Runnable mLockRunnable = new Runnable() {
        @Override
        public void run() {
            mOvalLockView.stopWave();
            mOvalLockView.changeLockState(true);
            mOvalLockView.setText("已上锁");
        }
    };

    private Runnable mUnlockRunnable = new Runnable() {
        @Override
        public void run() {
            mOvalLockView.stopWave();
            mOvalLockView.changeLockState(false);
            mOvalLockView.setText("未上锁");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOvalLockView = findViewById(R.id.lock_view);
        mOvalLockView.setBluetoothConnect(false);
        mOvalLockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOvalLockView.connecting(true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOvalLockView.connecting(false);
                        mOvalLockView.setBluetoothConnect(true);
                    }
                },2000);
//                Toast.makeText(MainActivity.this, "点击了", Toast.LENGTH_SHORT).show();
            }
        });

        if(mOvalLockView.isLock()){
            mOvalLockView.setText("已上锁");
        }else {
            mOvalLockView.setText("未上锁");
        }
        mOvalLockView.setOnLockOperateListener(new OnLockOperateListener() {
            @Override
            public void onLockPrepared() {
                mOvalLockView.setText("释放上锁");
            }

            @Override
            public void onUnLockPrepared() {
                mOvalLockView.setText("释放开锁");
            }

            @Override
            public void onLockStart() {
                mOvalLockView.setText("正在上锁");
                mOvalLockView.startWave();
                Message message = Message.obtain();
                message.what=1;
                mHandler.sendMessage(message);
                mHandler.postDelayed(mLockRunnable, 3000);
            }

            @Override
            public void onUnlockStart() {
                mOvalLockView.startWave();
                mOvalLockView.setText("正在开锁");
                Message message = Message.obtain();
                message.what=1;
                mHandler.sendMessage(message);
                mHandler.postDelayed(mUnlockRunnable, 3000);
            }
        });
    }
}
