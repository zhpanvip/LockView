package com.zhpan.lockview.utils;

import android.content.res.Resources;

/**
 * dp px转换
 */
public class LockViewUtils {

    public static int dp2px(float dpValue) {
        return (int) (0.5F + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }
}
