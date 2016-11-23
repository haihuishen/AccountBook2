package com.shen.accountbook2.Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import com.shen.accountbook2.global.AccountBookApplication;

public class UIUtils {

	/**
	 * 获取————主线程的上下文<p>
	 * return AccountBookApplication.getContext();
	 * @return	Context
	 */
	public static Context getContext() {
		return AccountBookApplication.getContext();
	}

	/**
	 * 获取————主线程的Handler<p>
	 * return AccountBookApplication.getHandler();
	 * @return	Handler
	 */
	public static Handler getHandler() {
		return AccountBookApplication.getHandler();
	}

	/**
	 * 获取————主线程的ID<p>
	 * return AccountBookApplication.getMainThreadId();
	 * @return	int
	 */
	public static int getMainThreadId() {
		return AccountBookApplication.getMainThreadId();
	}



	// /////////////////加载资源文件"/res/.../...xml "///////////////////////////

	/**
	 *  获取字符串<p>
	 *  获取：/AccountBook/res/values/strings.xml 里面的字符串
	 * @param id			string对应的id
	 * @return
	 */
	public static String getString(int id) {
		return getContext().getResources().getString(id);
	}

	/**
	 *  获取字符串数组<p>
	 *  获取：/AccountBook/res/values/strings.xml 里面的字符串数组
	 *  
	 *<string-array name="tab_names">
	 *	<item>首页</item>
	 *	<item>应用</item>
	 *	<item>游戏</item>
	 *	<item>专题</item>
	 *	<item>推荐</item>
	 *	<item>分类</item>
	 *	<item>排行</item>
	 *</string-array>
	 * @param id
	 * @return
	 */
	public static String[] getStringArray(int id) {
		return getContext().getResources().getStringArray(id);
	}

	/**
	 *  获取图片<p>
	 *  获取：/AccountBook/res/drawable/...xml 获取图片
	 * @param id
	 * @return
	 */
	public static Drawable getDrawable(int id) {
		return getContext().getResources().getDrawable(id);
	}

	/**
	 *  获取颜色<p>
	 *  获取：/AccountBook/res/values/colors.xml 获取颜色
	 * @param id
	 * @return
	 */
	public static int getColor(int id) {
		return getContext().getResources().getColor(id);
	}
	
	/**
	 * 根据id获取颜色的状态选择器<p>
	 *  获取：/AccountBook/res/color/...xml 获取颜色的状态选择器
	 * @param id
	 * @return
	 */
	public static ColorStateList getColorStateList(int id) {
		return getContext().getResources().getColorStateList(id);
	}

	/**
	 *  获取尺寸<p>
	 *  获取：/AccountBook/res/values/dimens.xml 获取尺寸
	 * @param id
	 * @return
	 */
	public static int getDimen(int id) {
		return getContext().getResources().getDimensionPixelSize(id);// 返回具体像素值
	}

	// /////////////////dip和px转换//////////////////////////

	/**
	 * dip————px
	 * @param dip		要转换的dip  (float)
	 * @return			int
	 */
	public static int dip2px(float dip) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * density + 0.5f);
	}

	/**
	 * px————dip
	 * @param px		要转换的px  (int)
	 * @return			float
	 */
	public static float px2dip(int px) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return px / density;
	}

	// /////////////////加载布局文件//////////////////////////
	/**
	 * 加载布局文件<p>
	 * return View.inflate(getContext(), id, null);
	 * 
	 * @param id
	 * @return		View
	 */
	public static View inflate(int id) {
		return View.inflate(getContext(), id, null);
	}

	// /////////////////判断是否运行在主线程//////////////////////////
	/**
	 * 判断是否运行在主线程
	 * 
	 * @return	boolean
	 */
	public static boolean isRunOnUIThread() {
		// 获取当前线程id, 如果当前线程id和主线程id相同, 那么当前就是主线程
		// ***
		int myTid = android.os.Process.myTid();
		
		if (myTid == getMainThreadId()) {
			return true;
		}

		return false;
	}

	/**
	 *  运行在主线程
	 *  public static void runOnUIThread(Runnable r) {
	 *  		if (isRunOnUIThread()) {
	 *  			// 已经是主线程, 直接运行
	 *  			r.run();
	 *  		} else {
	 *  			// 如果是子线程, 借助handler让其运行在主线程
	 *  			getHandler().post(r);
	 *  		}
	 *  }
	 * @param r
	 */
	public static void runOnUIThread(Runnable r) {
		if (isRunOnUIThread()) {
			// 已经是主线程, 直接运行
			r.run();
		} else {
			// 如果是子线程, 借助handler让其运行在主线程
			getHandler().post(r);
		}
	}

}
