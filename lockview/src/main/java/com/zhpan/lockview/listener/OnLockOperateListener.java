package com.zhpan.lockview.listener;

public interface  OnLockOperateListener {
   void onLockPrepared();

   void onUnLockPrepared();

   void onLockStart();

   void onUnlockStart();

   void onNotPrepared();
}
