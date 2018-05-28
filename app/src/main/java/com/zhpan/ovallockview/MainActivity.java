package com.zhpan.ovallockview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhpan.ovallockview.listener.OnLockOperateListener;
import com.zhpan.ovallockview.listener.OnLockViewClickListener;
import com.zhpan.ovallockview.view.OvalLockView;

public class MainActivity extends AppCompatActivity {
    private OvalLockView mOvalLockView;
    private static Handler mHandler=new Handler();
    private Runnable mRunnable=new Runnable() {
        @Override
        public void run() {
            mOvalLockView.stopWave();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOvalLockView=findViewById(R.id.lock_view);
        mOvalLockView.setOnLockOperateListener(new OnLockOperateListener() {
            @Override
            public void onLockStart() {
                Toast.makeText(MainActivity.this, "正在上锁", Toast.LENGTH_SHORT).show();
                mOvalLockView.startWave();
                mHandler.postDelayed(mRunnable,3000);
            }

            @Override
            public void onUnlockStart() {
                Toast.makeText(MainActivity.this, "正在开锁", Toast.LENGTH_SHORT).show();
                mOvalLockView.startWave();
                mHandler.postDelayed(mRunnable,3000);
            }

            @Override
            public void onLockPrepared() {
                mOvalLockView.setText("释放上锁");
                mOvalLockView.setCircleColor(getResources().getColor(R.color.red));
            }

            @Override
            public void onUnLockPrepared() {
                mOvalLockView.setText("释放开锁");
                mOvalLockView.setCircleColor(getResources().getColor(R.color.green));
            }
        });

        mOvalLockView.setOnLockViewClickListener(new OnLockViewClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(MainActivity.this, "点击了", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
