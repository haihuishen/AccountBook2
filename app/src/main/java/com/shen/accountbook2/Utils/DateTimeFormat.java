package com.shen.accountbook2.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shen on 11/9 0009.
 */
public class DateTimeFormat {

    /**
     *  将传进来的"时间"，按照一定的格式生成"时间字符串"
     * @param date      传进来的时间
     * @param format    格式：   yyyy-MM-dd HH:mm
     * @return
     */
    public static String getTime(Date date, String format) {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
