package com.shen.accountbook2.Utils.BitmapUtils;

import android.content.Context;

import com.shen.accountbook2.Utils.SharePrefUtil;

/**
 * 网络缓存工具类
 * 
 * @author Kevin
 * @date 2015-10-18
 */
public class CacheUtils {

	/**
	 * 以url为key, 以json为value,保存在本地 "SharedPreferences"中<p>
	 * 
	 * 注：<br>
	 * 实际上我们添加的缓存是,json数据<br>
	 * 如果图片之类了,是Android(ImageView)帮我们做缓存的<br>
	 * "mnt/sdcard/Android/data/包名/cache/xBitmapCache/文件"<br>
	 * (应该是,地址"json数据中图片的地址"和"图片数据"写到一个文件)
	 * 
	 * @param url		接口（网址,json地址;如果地址带参数的也要写上）
	 * @param json
	 */
	public static void setCache(String url, String json, Context ctx) {
		//也可以用文件缓存: 以MD5(url)为文件名, 以json为文件内容
		SharePrefUtil.saveString(ctx, url, json);
	}

	/**
	 * "SharedPreferences"中 获取缓存 (得到String   json数据)<p>
	 * 
	 * 注：<br>
	 * 实际上我们添加的缓存是,json数据<br>
	 * 如果图片之类了,是Android(ImageView)帮我们做缓存的<br>
	 * "mnt/sdcard/Android/data/包名/cache/xBitmapCache/文件"<br>
	 * (应该是,地址"json数据中图片的地址"和"图片数据"写到一个文件)
	 * 
	 * @param url		接口（网址,json地址;如果地址带参数的也要写上）
	 * @param ctx
	 * @return			String  json数据
	 */
	public static String getCache(String url, Context ctx) {
		//也可以使用文件缓存: 查找有没有一个文件叫做MD5(url)的, 有的话,说明有缓存
		return SharePrefUtil.getString(ctx, url, null);
	}
}
