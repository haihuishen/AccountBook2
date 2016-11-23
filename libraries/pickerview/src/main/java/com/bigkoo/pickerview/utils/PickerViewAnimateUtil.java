package com.bigkoo.pickerview.utils;

import android.view.Gravity;

import com.bigkoo.pickerview.R;

/**
 * Created by Sai on 15/8/9.
 */
public class PickerViewAnimateUtil {
    private static final int INVALID = -1;
    /**
     * Get default animation resource when not defined by the user
     *时得到默认动画资源不是由用户定义
     * @param gravity       the gravity of the dialog 对话框的重心
     * @param isInAnimation determine if is in or out animation. true when is is  确定或动画。真正的是什么时候
     * @return the id of the animation resource 动画资源的id
     */
    public static int getAnimationResource(int gravity, boolean isInAnimation) {
        switch (gravity) {
//            推动物体的容器底部,不改变其大小。
//            恒定值:80(0 x00000050)
            case Gravity.BOTTOM:
                return isInAnimation ? R.anim.slide_in_bottom : R.anim.slide_out_bottom;
        }
        return INVALID;
    }
}
/** anim/slide_in_bottom.xml */
//<?xml version="1.0" encoding="utf-8"?>
//<set xmlns:android="http://schemas.android.com/apk/res/android"
//        android:shareInterpolator="false">
//
//<translate   -- 翻译，转化
//        android:duration="300"
//        android:fromXDelta="0%"
//        android:toXDelta="0%"
//        android:fromYDelta="100%"
//        android:toYDelta="0%"/>
//</set>
