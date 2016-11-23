package com.shen.accountbook2.clander;

import android.content.Context;

/**
 *
 * 分辨率 单位转换<br>
 * 全局变量
 */
public class Constants {

    /** 被选中的"年"*/
    public static String zYear = "";
    /** 被选中的"月"*/
    public static String zMonth = "";
    /** 被选中的"日"*/
    public static String zDay = "";

    /**
     * 如果 少于或者等于35天显示五行 多余35天显示六行<br>
     * 五行: 收缩比例是：0.25，0.5，0.75，1<br>
     * 六行: 收缩比例是：0.2，0.4，0.6，0.8，1<br>
     * 每行星期1到7
     */
    public static float scale = 0.2f;   // 缩放比例


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
