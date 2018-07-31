package com.zhpan.lockview.listener;

public interface  OnLockOperateListener {
   // 上锁就绪
   void onLockPrepared();
   // 开锁就绪
   void onUnLockPrepared();
   // 开始上锁
   void onLockStart();
   // 开始开锁
   void onUnlockStart();
   // 未就绪
   void onNotPrepared();
}
