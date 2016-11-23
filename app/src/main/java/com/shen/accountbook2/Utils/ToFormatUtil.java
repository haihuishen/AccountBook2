package com.shen.accountbook2.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by shen on 9/13 0013.
 */
public class ToFormatUtil {


    /**
     * 根据传递进来的 src 源数据(String)， 和截取的位数number，截取小数点<p>
     *     如： toDecimalFormat(2，3) ==> 2.000
     *
     * @param src       要转换的数据(float)
     * @param number   小数点位数(1~7位;超过默认是2位)
     * @return
     */
    public static String toDecimalFormat(float src, int number){

        // 0是特殊模式字符，没有数字填充时，默认填写0。
        // 0  数字 是 阿拉伯数字
        // #  数字 是 阿拉伯数字，如果不存在则显示为空
        // .  数字 是 小数分隔符或货币小数分隔符
        String n = "";
        switch (number){
            case 0: n = "#0"; break;
            case 1: n = "#0.0"; break;
            case 2: n = "#0.00"; break;
            case 3: n = "#0.000"; break;
            case 4: n = "#0.0000"; break;
            case 5: n = "#0.00000"; break;
            case 6: n = "#0.000000"; break;
            case 7: n = "#0.0000000"; break;
            default: n = "#0.00"; break;
        }
        DecimalFormat df=new DecimalFormat(n);
        String a= df.format(src);

        return a;
    }

    /**
     * 根据传递进来的 src 源数据(String)， 和截取的位数number，截取小数点<p>
     *     如： stringToDecimalFormat(2，3) ==> 2.000
     *
     * @param src       要转换的数据(String)
     * @param number   小数点位数(1~7位;超过默认是2位)
     * @return
     */
    public static String stringToDecimalFormat(String src, int number){

        String n = "";
        switch (number){
            case 0: n = "#0"; break;
            case 1: n = "#0.0"; break;
            case 2: n = "#0.00"; break;
            case 3: n = "#0.000"; break;
            case 4: n = "#0.0000"; break;
            case 5: n = "#0.00000"; break;
            case 6: n = "#0.000000"; break;
            case 7: n = "#0.0000000"; break;
            default: n = "#0.00"; break;
        }


        DecimalFormat df=new DecimalFormat(n);
        String a= df.format(Float.valueOf(src));

        return a;
    }


    /**
     * long补零,返回的是String<p>
     *     如： toNumberFormat(2，3) ==> 002
     * @param src          要补零的(int)
     * @param number       总位数
     * @return
     */
    public static String toNumberFormat(long src, int number){

        long m = src;
        NumberFormat nf = NumberFormat.getInstance();   // 得到一个NumberFormat的实例
        nf.setGroupingUsed(false);                      // 设置是否使用分组
        nf.setMaximumIntegerDigits(number);                  // 设置最大整数位数
        nf.setMinimumIntegerDigits(number);                  // 设置最小整数位数

        String data = nf.format(m);

        return data;
    }


    /**
     * String 转换成long再补零,返回的是String<p>
     *     如： stringToNumberFormat(2，3) ==> 002
     * @param src          要补零的(int)
     * @param number       总位数
     * @return
     */
    public static String stringToNumberFormat(String src, int number){

        NumberFormat nf = NumberFormat.getInstance();   // 得到一个NumberFormat的实例
        nf.setGroupingUsed(false);                      // 设置是否使用分组
        nf.setMaximumIntegerDigits(number);                  // 设置最大整数位数
        nf.setMinimumIntegerDigits(number);                  // 设置最小整数位数

        String data = nf.format(Long.valueOf(src));

        return data;
    }

}
