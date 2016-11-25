package com.shen.accountbook2.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 跟App相关的辅助类
 *
 */
public class AppUtils
{

    private AppUtils()
    {
		/** cannot be instantiated : 不能被实例化 */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context)
    {
        try
        {
            //1,包管理者对象packageManager
            PackageManager packageManager = context.getPackageManager();
            //2,从包的管理者对象中,获取指定包名的基本信息(版本名称,版本号),传0代表获取基本信息
            //getPackageName()得到本应用的包名
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]<p>
     *
     * 1,包管理者对象packageManager——<br>
     * 2,从包的管理者对象中,获取指定包名的基本信息(版本名称,版本号)——"PackageInfo"——<br>
     * getPackageName()得到本应用的包名<br>
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回版本号<p>
     * 1,包管理者对象packageManager——<br>
     * 2,从包的管理者对象中,获取指定包名的基本信息(版本名称,版本号)——"PackageInfo"——<br>
     * getPackageName()得到本应用的包名<br>
     *
     * @param context
     * @return
     * 			非0 则代表获取成功
     */
    public static int getVersionCode(Context context) {
        //1,包管理者对象packageManager
        PackageManager packageManager = context.getPackageManager();
        //2,从包的管理者对象中,获取指定包名的基本信息(版本名称,版本号),传0代表获取基本信息
        //getPackageName()得到本应用的包名
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            //3,获取版本名称
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
