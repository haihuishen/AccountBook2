package com.shen.accountbook2.Utils;

//import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreferences操作工具类
 */
public class SharePrefUtil {
	private static String tag = SharePrefUtil.class.getSimpleName();   // 好像没用
	private final static String SP_NAME = "config";
	private static SharedPreferences sp;

	public interface KEY {
        /** 记住登录密码*/
 		String REMEMBER_ISCHECK = "remember_isChecked" ;  // 记住登录密码
        /** 自动登录*/
		String AUTO_ISCHECK = "auto_isChecked" ;  			// 自动登录
        /** 用户名*/
		String USENAME = "usename";							// 用户名
        /** 密码*/
		String PASSWORK = "password";							// 密码
        /** 正在登录*/
        String LOGINING = "logining";                 // 正在登录
        /** 是否登录了 false==>login  true==>logout*/
        String LONGIN_OR_LONGOUT = "login_or_logout";
	}

    public interface REPORT_KEY{
        /** 在"节点(峰点)"中显示"文本(峰点值)"*/
        String TOGGLELABELS_MONTH_ISCHECK = "toggleLabels_month_isChecked";     // 在"节点(峰点)"中显示"文本(峰点值)"
        /** 区域的填充颜色(图形和坐标轴包围的区域)*/
        String TOGGLEFILLED_MONTH_ISCHECK = "toggleFilled_month_isChecked";     // 区域的填充颜色(图形和坐标轴包围的区域)
        /** 是否允许，触摸放大*/
        String SELECTYX_MONTH_ISCHECK = "selectYX_month_isChecked";             // 是否允许，触摸放大
        /** 触摸放大类型*/
        String SELECTYX_MONTH_TYPE = "selectYX_month_type";                     // 是触摸放大类型


        /** 在"节点(峰点)"中显示"文本(峰点值)"*/
        String TOGGLELABELS_YEAR_ISCHECK = "toggleLabels_year_isChecked";     // 在"节点(峰点)"中显示"文本(峰点值)"
        /** 区域的填充颜色(图形和坐标轴包围的区域)*/
        String TOGGLEFILLED_YEAR_ISCHECK = "toggleFilled_year_isChecked";     // 区域的填充颜色(图形和坐标轴包围的区域)
        /** 是否允许，触摸放大*/
        String SELECTYX_YEAR_ISCHECK = "selectYX_year_isChecked";             // 是否允许，触摸放大
        /** 触摸放大类型*/
        String SELECTYX_YEAR_TYPE = "selectYX_year_type";                     // 是触摸放大类型


        /** 在"节点(峰点)"中显示"文本(峰点值)"*/
        String TOGGLELABELS_TYPE_ISCHECK = "toggleLabels_type_isChecked";     // 在"节点(峰点)"中显示"文本(峰点值)"
        /** 区域的填充颜色(图形和坐标轴包围的区域)*/
        String TOGGLEFILLED_TYPE_ISCHECK = "toggleFilled_type_isChecked";     // 区域的填充颜色(图形和坐标轴包围的区域)
        /** 是否允许，触摸放大*/
        String SELECTYX_TYPE_ISCHECK = "selectYX_type_isChecked";             // 是否允许，触摸放大
        /** 触摸放大类型*/
        String SELECTYX_TYPE_TYPE = "selectYX_type_type";                     // 是触摸放大类型
    }

    public interface IMAGE_KEY{
        /** 添加时，是否添加图片*/
        String IS_ADD_IMAGE = "is_add_image";                           // 添加时，是否添加图片
    }

    public interface REPORT_KEY_2{
        /** 年份/月份*/
        String YEAR_MONTH = "year_month";                               //  年份/月份
        /** 类型--日期*/
        String TYPE_MONTHANDDAY = "type_monthAndday";                  //  类型--日期

    }

    public interface REPORT_KEY_3{
        /** 类型--日期*/
        String MAINTYPE_OR_TYPE1 = "maintype_or_type1";                  //  主类型/次类型

    }


	public interface HELP_PAGE_KEY {

		String HELP_PAGE_FUNCTION = "help_page_function";// Funcation 帮助界面
		String HELP_PAGE_NEWS = "help_page_news";// NEWS 帮助界面
		String HELP_PAGE_SERVICE = "help_page_service";// SERVICE 帮助界面
		String HELP_PAGE_GOV = "help_page_gov";// GOV 帮助界面
	}

	/**
	 * 保存布尔值
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(Context context, String key, boolean value) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putBoolean(key, value).commit();
	}

	/**
	 * 保存字符串
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveString(Context context, String key, String value) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putString(key, value).commit();

	}

    /**
     * 将当前的 SharedPreferences清空
     *
     * @param context
     */
	public static void clear(Context context) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().clear().commit();
	}

	/**
	 * 保存long型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveLong(Context context, String key, long value) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putLong(key, value).commit();
	}

	/**
	 * 保存int型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveInt(Context context, String key, int value) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putInt(key, value).commit();
	}

	/**
	 * 保存float型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveFloat(Context context, String key, float value) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putFloat(key, value).commit();
	}


	/**
	 * 获取字符值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getString(key, defValue);
	}

	/**
	 * 获取int值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String key, int defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getInt(key, defValue);
	}

	/**
	 * 获取long值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static long getLong(Context context, String key, long defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getLong(key, defValue);
	}

	/**
	 * 获取float值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static float getFloat(Context context, String key, float defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getFloat(key, defValue);
	}

	/**
	 * 获取布尔值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getBoolean(key, defValue);
	}

//	/**
//	 * 将对象进行base64编码后保存到SharePref中
//	 *
//	 * @param context
//	 * @param key
//	 * @param object
//	 */
//	public static void saveObj(Context context, String key, Object object) {
//		if (sp == null)
//			sp = context.getSharedPreferences(SP_NAME, 0);
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ObjectOutputStream oos = null;
//		try {
//			oos = new ObjectOutputStream(baos);
//			oos.writeObject(object);
//			// 将对象的转为base64码
//			String objBase64 = new String(Base64.encodeBase64(baos
//					.toByteArray()));
//
//			sp.edit().putString(key, objBase64).commit();
//			oos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	/**
//	 * 将SharePref中经过base64编码的对象读取出来
//	 *
//	 * @param context
//	 * @param key
//	 * @param defValue
//	 * @return
//	 */
//	public static Object getObj(Context context, String key) {
//		if (sp == null)
//			sp = context.getSharedPreferences(SP_NAME, 0);
//		String objBase64 = sp.getString(key, null);
//		if (TextUtils.isEmpty(objBase64))
//			return null;
//
//		// 对Base64格式的字符串进行解码
//		byte[] base64Bytes = Base64.decodeBase64(objBase64.getBytes());
//		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
//
//		ObjectInputStream ois;
//		Object obj = null;
//		try {
//			ois = new ObjectInputStream(bais);
//			obj = (Object) ois.readObject();
//			ois.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return obj;
//	}

}
