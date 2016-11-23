package com.shen.accountbook2.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by shen on 11/9 0009.
 */
public class TimeCompare {
    /*时间比大小*/

    /**
     * 时间比大小
     * @param t1  String时间字符串
     * @param t2  String时间字符串
     * @return 0:时间相等; 1:t1>t2; -1:t1<t2
     */
    public static int timeCompare(String t1,String t2){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1=Calendar.getInstance();
        Calendar c2=Calendar.getInstance();
        try {
            c1.setTime(formatter.parse(t1));
            c2.setTime(formatter.parse(t2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int result=c1.compareTo(c2);
        return result;
    }



//    compareTo()的返回值是整型,它是先比较对应字符的大小(ASCII码顺序),如果第一个字符和参数的第一个字符不等,结束比较,返回他们之间的
//
//            差值,如果第一个字符和参数的第一个字符相等,则以第二个字符和参数的第二个字符做比较,以此类推,直至比较的字符或被比较的字符有一方
//
//    全比较完,这时就比较字符的长度.
//
//            例:
//    String s1 = "abc";
//    String s2 = "abcd";
//    String s3 = "abcdfg";
//    String s4 = "1bcdfg";
//    String s5 = "cdfg";
//    System.out.println( s1.compareTo(s2) ); // -1 (前面相等,s1长度小1)
//    System.out.println( s1.compareTo(s3) ); // -3 (前面相等,s1长度小3)
//    System.out.println( s1.compareTo(s4) ); // 48 ("a"的ASCII码是97,"1"的的ASCII码是49,所以返回48)
//    System.out.println( s1.compareTo(s5) ); // -2 ("a"的ASCII码是97,"c"的ASCII码是99,所以返回-2)


//    但这里的是
/**    java.util.Calendar.compareTo() 方法比较Calendar对象和anotherCalendar对象之间的时间值(毫秒偏移量)。*/
//    声明
//    以下是java.util.Calendar.compareTo()方法的声明
//
//    public int compareTo(Calendar anotherCalendar)
//    参数
//    anotherCalendar -- 要比较的Calendar对象。
//
//    返回值
//            如果参数所代表的时间等于通过此Calendar对象表示的时间方法返回0;
//    或如果此Calendar的时间是由参数表示的时间之前返回小于0值，
//    或如果该日历的时间所表示的时间之后返回大于0值。
//    异常
//    NullPointerException - 如果指定的Calendar为null。
//    IllegalArgumentException - 如果不能得到指定的日历Calendar 对象的时间值
}
