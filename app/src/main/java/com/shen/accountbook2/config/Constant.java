package com.shen.accountbook2.config;

import android.os.Environment;

import java.io.File;

/**
 * 全局变量
 */
public class Constant {

    /** 数据库名 */
	public final static String DB_NAME = "AccountBook2.db";
    // AccountBook2.db数据库的"各种表"
    public final static String TABLE_USER = "user";
    public final static String TABLE_CONSUMPTION = "consumption";
    public final static String TABLE_ASSETS = "assets";

    // consumption表的字段(索引)
    public final static int TABLE_CONSUMPTION__id = 0;
    public final static int TABLE_CONSUMPTION_user = 1;
    public final static int TABLE_CONSUMPTION_maintype = 2;
    public final static int TABLE_CONSUMPTION_type1 = 3;
    public final static int TABLE_CONSUMPTION_concreteness = 4;
    public final static int TABLE_CONSUMPTION_price = 5;
    public final static int TABLE_CONSUMPTION_number = 6;
    public final static int TABLE_CONSUMPTION_unitprice = 7;
    public final static int TABLE_CONSUMPTION_image = 8;
    public final static int TABLE_CONSUMPTION_date = 9;

    // consumption表的字段(字符)
    public final static String TABLE_CONSUMPTION__id_STRING = "_id";
    public final static String TABLE_CONSUMPTION_user_STRING = "user";
    public final static String TABLE_CONSUMPTION_maintype_STRING = "maintype";
    public final static String TABLE_CONSUMPTION_type1_STRING = "type1";
    public final static String TABLE_CONSUMPTION_concreteness_STRING = "concreteness";
    public final static String TABLE_CONSUMPTION_price_STRING = "price";
    public final static String TABLE_CONSUMPTION_number_STRING = "number";
    public final static String TABLE_CONSUMPTION_unitprice_STRING = "unitprice";
    public final static String TABLE_CONSUMPTION_image_STRING = "image";
    public final static String TABLE_CONSUMPTION_date_STRING = "date";

    // asset类型
    public final static String CREDIT = "信用卡";
    public final static String DEPOSIT = "储蓄卡";
    public final static String COMPANY = "借贷公司";
    public final static String ECPSS = "第三方支付";
    public final static String OWEOTHER = "欠别人钱";
    public final static String OWEME = "我是债主";
    public final static String ME = "个人现金";



    /**
     * 资源文件存放地址    <p>
     *
     *  /storage/emulated/0/AccountBook2/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String APP_RESOURCE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "AccountBook2" + File.separator ;
    /**
     * CacheImage图片文件存放地址    <p>
     *
     *  /storage/emulated/0/AccountBook2/CacheImage/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String CACHE_IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "AccountBook2" + File.separator + "CacheImage" + File.separator;


    /**
     * CacheImage.jpg图片文件存放地址    <p>
     *
     *  /storage/emulated/0/AccountBook2/CacheImage/CacheImage.jpg <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String CACHE_IMAGE_PATH_CacheImage = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "AccountBook2" + File.separator + "CacheImage" + File.separator + "CacheImage.jpg";

    /**
     * CurrentImage.png截图文件存放的地方    <p>
     *
     *  /storage/emulated/0/AccountBook2/CacheImage/CurrentImage.png <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String CACHE_IMAGE_PATH_CurrentImage = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "AccountBook2" + File.separator + "CacheImage" + File.separator + "CurrentImage.png";


    /**
     * Image图片文件存放地址   <p>
     *
     *  /storage/emulated/0/AccountBook2/Image/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "AccountBook2" + File.separator + "Image" + File.separator;


    /**
     * Image图片缓存地址   <p>
     *
     *  /storage/emulated/0/AccountBook2/AccountBook2_cache/ <br>
     *  如果使用"/"==>File.separator<p>
     */
    public final static String LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "AccountBook2" + File.separator + "AccountBook2_local_cache" + File.separator;


    /**
     * 服务器主域名
     */
    public static final String SERVER_URL = "http://192.168.23.1:8080/AccountBook2";

    /**
     * public static final String SERVER_URL = "http://10.0.2.2:8080/zhbj"
     * <br>服务器主域名
     *
     * <p>public static final String PHOTOS_URL = SERVER_URL + "/photos/photos_1.json";
     * <br>组图信息接口
     */
    public static final String PHOTOS_URL = SERVER_URL + "/photos/photos_1.json";


}
