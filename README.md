# OvalLockView
动画效果
![这里写图片描述](https://github.com/zhpanvip/OvalLockView/blob/master/image/yanshi.gif)


使用方法：
1.布局文件添加
  ```
  <com.zhpan.lockview.view.LockView
          android:id="@+id/lock_view"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:layout_centerInParent="true" />
  ```
  2.设置操作的监听事件
   ```
  mLockView.setOnLockOperateListener(new OnLockOperateListener() {
            @Override
            public void onLockPrepared() {//  准备上锁
                
            }

            @Override
            public void onUnLockPrepared() {//  准备开锁

            }

            @Override
            public void onLockStart() {// 开始上锁

            }

            @Override
            public void onUnlockStart() {// 开始开锁

            }

            @Override
            public void onNotPrepared() {// 上下滑动距离未达到准备上锁或者准备开锁状态而释放

            }
        });
  ```
  3.开放接口
   ```
   // 设置蓝牙是否连接
   mLockView.setBluetoothConnect(false);
   // 设置上锁状态
   mLockView.setLockState(isLock);
   // 设置View是否可以滑动
   mLockView.setCanSlide(true)
   // 设置滑动阻尼大小
   mLockView.setDamping(1.7)
   // 设置View中心文字
   mLockView.setText("已上锁");
   // 设置中心大圆的颜色
   mLockView.setCircleColor
   // 开启心跳动画
   mLockView.startWave();
   // 停止心跳动画
   mLockView.stopWave();
   // 是否正在搜索/连接蓝牙
   mLockView.connecting(true);
   
   // 点击事件监听（只有在未连接蓝牙时有效）
   mLockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
   ```
  
