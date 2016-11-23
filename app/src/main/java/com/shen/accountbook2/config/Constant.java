package com.shen.accountbook2.config;

import android.os.Environment;

import com.shen.accountbook2.domain.MainTypeInfo;

import java.util.List;

/**
 * 全局变量
 */
public class Constant {

    /** 数据库名 */
	public final static String DB_NAME = "AccountBook2.db";
    // AccountBook2.db数据库的"各种表"
    public final static String TABLE_USER = "user";
    public final static String TABLE_MAINTYPE = "MainType";
    public final static String TABLE_TYPE1 = "Type1";
    public final static String TABLE_CONSUMPTION = "consumption";
    public final static String TABLE_ASSETS = "assets";

    // consumption表的字段
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

    // consumption表的字段
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


//    // activity 跳转请求：
//    public static final String ADD_ASSET= "addAsset";
//    public static final String CHANGE_ASSET= "changeAsset";

    public final static String[] dialogYear = new String[]{
            "1980","1981","1982","1983","1984","1985","1986","1987","1988","1989", 
            "1990","1991","1992","1993","1994","1995","1996","1997","1998","1999",
            "2000","2001","2002","2003","2004","2005","2006","2007","2008","2009", 
            "2010","2011","2012","2013","2014","2015","2016","2017","2018","2019",
            "2020","2021","2022","2023","2024","2025","2026","2027","2028","2029",
            "2030","2031","2032","2033","2034","2035","2036","2037","2038","2039",
    };

    public final static String[] dialogMonth = new String[]{
            "01","02","03","04","05","06","07","08","09","10","11","12"
    };


    public final static List<MainTypeInfo> mainTypeList = null;

    /** 图片文件存放地址1*/
    public final static String CACHE_IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/AccountBook2/CacheImage";
    /** 图片文件存放地址2*/
    public final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/AccountBook2/Image";

}
