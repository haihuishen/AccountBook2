package com.shen.accountbook2.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.shen.accountbook2.domain.UserInfo;
import com.shen.accountbook2.xml.model.MainTypeBean;

import java.util.ArrayList;
import java.util.Map;


/**
 * 自定义application, 进行全局初始化
 * 这里将这个类作为全局初始化，程序运行时就已经初始化了
 * 在类中定义用于保存重要信息的变量，
 *	如：
 *		登陆成功后把唯一标示保存进去，整个应用程序都可以取到这个值了
 *
 *	使用：
 *		MyApplication.getContext();
 *
 * 全局要这样在"清单文件"中声明!
 *
 *  <application>
 *      android:name="com.xxx.xxx.global.MyApplication"   ===> 这样就全局化了
 *
 *      android:allowBackup="true"
 *      android:icon="@drawable/ic_launcher"
 *      android:label="@string/app_name"
 *      android:theme="@style/Theme.AppCompat.Light"
 *      <activity
 *      	。。。。。。。。。。。。。
 *      </activity>
 *  </application>
 *
 *  如果不使用，static
 *      首先调用本类的getInstance()得到AccountBookApplication的对象
 *      就要使用 ：AccountBookApplication.getInstance().getXXX();
 *
 */
public class AccountBookApplication extends Application {

    private static Context context;
    private static Handler handler;
    /** 主线程的ID*/
    private static int mainThreadId;

    private static boolean isLogin;         // 登录状态
    private static UserInfo userInfo;        // 登录后用户信息

    private static Map<String,MainTypeBean> mainTypeBeanMap;    // 存放 mainTypeBean的 HashMap
    private static ArrayList<String> mainTypeList;                           // 存放 mainType 的 List
    private static ArrayList<ArrayList<String>> listType1List;  // 获得所有的mainType 对应的 type1列表--再放到一个 ArrayList

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();
        mainThreadId = android.os.Process.myTid();	// 获取当前线程的id
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    /**
     * 获取————主线程的ID
     * @return
     */
    public static int getMainThreadId() {
        return mainThreadId;
    }


    /**********************************登录信息******************************************/
    /**
     * 返回登录状态
     * @return
     */
    public static boolean isLogin() {
        return isLogin;
    }

    /**
     * 设置当前有没有登录
     * @param isLogin   登录标志
     */
    public static void setIsLogin(boolean isLogin) {
        AccountBookApplication.isLogin = isLogin;
    }

    /**
     * 获取登录后用户信息
     * @return
     */
    public static UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 设置登录后用户信息
     * @param userInfo
     */
    public static void setUserInfo(UserInfo userInfo) {
        AccountBookApplication.userInfo = userInfo;
    }

    /**********************************消费类型******************************************/

    /**
     * 获取 MainType列表
     * @return
     */
    public static ArrayList<String> getMainTypeList() {
        return mainTypeList;
    }

    /**
     * 设置一个MainType列表
     * @param mainTypeList
     */
    public static void setMainTypeList(ArrayList<String> mainTypeList) {
        AccountBookApplication.mainTypeList = mainTypeList;
    }


    /**
     * 获得所有的mainType 对应的 type1列表--再放到一个 ArrayList
     * @return
     */
    public static ArrayList<ArrayList<String>> getListType1List() {
        return  listType1List;
    }

    /**
     * 设置一个ArrayList<ArrayList<String>>
     * @return
     */
    public static void setListType1List(ArrayList<ArrayList<String>> listType1List) {
        AccountBookApplication.listType1List = listType1List;
    }


    /**
     * 获取存放 MainTypeBean的 HashMap
     * @return
     */
    public static Map<String, MainTypeBean> getMainTypeBeanMap() {
        return mainTypeBeanMap;
    }



    /**
     * 设置一个 存放mainTypeBean 的 haspMap
     * <p>
     * 还将 mainType 弄出来，弄成 List
     * @param mainTypeBeanMap
     */
    public static void setMainTypeBeanMap(Map<String, MainTypeBean> mainTypeBeanMap) {
        AccountBookApplication.mainTypeBeanMap = mainTypeBeanMap;

        // 通过mainType来获取对应的 type1列表
        // 再添加到  "存放Type1List" 的列表中
        setListType1List(new ArrayList<ArrayList<String>>());
        for(String mainType : getMainTypeList()){
            getListType1List().add(getType1List(mainType));
        }
    }

    /**
     * 通过mainType来获取对应的 type1列表
     * @param mainType
     * @return
     */
    public static ArrayList<String> getType1List(String mainType) {

        if(!TextUtils.isEmpty(mainType))
           return mainTypeBeanMap.get(mainType).getType1List();
        else
            return null;
    }


}
