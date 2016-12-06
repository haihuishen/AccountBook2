package com.shen.accountbook2.Utils.BitmapUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.shen.accountbook2.config.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 本地缓存
 * 
 * 
 */
public class LocalCacheUtils {

	/**
	 * 本地缓存的地址<p>
	 * 
	 *Environment.getExternalStorageDirectory().getAbsolutePath() + "/AccountBook2/AccountBook2_cache/";<br>
	 *  /storage/emulated/0/AccountBook2/AccountBook2_cache/ <br>
	 */
	private static final String LOCAL_CACHE_PATH = Constant.LOCAL_CACHE_PATH;

	/**
	 *  写本地缓存
	 *  
	 * @param url				这个URL会md5加密保存
	 * @param bitmap			图片会压缩后保存
	 */
	public void setLocalCache(String url, Bitmap bitmap) {
		
		// 创建一个本地缓存的(总文件夹)
		File dir = new File(LOCAL_CACHE_PATH);
		
		// 判断文件是否存在
		// 判断这个文件是否是"文件夹"
		// ***不是就"创建文件夹"
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();// 创建文件夹
		}

		try {
			
			String fileName = MD5Encoder.encode(url);		// MD5后，才做为文件名

			// 在总文件夹中，创建fileName文件
			File cacheFile = new File(dir, fileName);

			// 图片压缩
			// ***参1:图片格式
			// ***参2:压缩比例0-100; %
			// ***参3:输出流 (压缩到哪里)
			bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(cacheFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  读本地缓存<p>
	 *  得到图片
	 *  
	 * @param url		网址
	 * @return			Bitmap
	 */
	public Bitmap getLocalCache(String url) {
		try {
			
			// 得到文件
			// 网址要md5,才做为文件名
			File cacheFile = new File(LOCAL_CACHE_PATH, MD5Encoder.encode(url));

			// 文件不为空
			if (cacheFile.exists()) {
				// 读出文件直接加载成 位图
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
