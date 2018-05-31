package com.zhpan.ovallockview.listener;

public interface  OnLockOperateListener {
   void onLockPrepared();

   void onUnLockPrepared();

   void onLockStart();

   void onUnlockStart();

   void onNotPrepared();
}
