package com.shen.accountbook2.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.CreateFilesUtils;
import com.shen.accountbook2.Utils.LogUtils;
import com.shen.accountbook2.Utils.MemorySizeUtils;
import com.shen.accountbook2.Utils.SharePrefUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.domain.UserInfo;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.xml.PullTypeParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 闪屏<p>
 *
 * Splash [splæʃ] vt.溅，泼；用...使液体飞溅	n.飞溅的水；污点；卖弄		vi.溅湿；溅开<p>
 *
 * 现在大部分APP都有Splash界面，下面列一下Splash页面的几个作用：<p>
 * 1、展示logo,提高公司形象<br>
 * 2、初始化数据 (拷贝数据到SD)<br>
 * 3、提高用户体验 <br>
 * 4、连接服务器是否有新的版本等。<br>
 *
 *     //implements UncaughtExceptionHandler
 *     //在onCreate()调用下面方法，才能捕获到线程中的异常
 *      Thread.setDefaultUncaughtExceptionHandler(this);
 */
public class SplashActivity extends Activity implements Thread.UncaughtExceptionHandler {


    private Handler handler = AccountBookApplication.getHandler();
    private Runnable runnable;

    private TextView mTvCountDown;//tv_count_down
    MyCountDownTimer myCountDownTimer;  // 倒计时器

    private TableEx mTableEx = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(runnable = new Runnable()                   // 发送个消息(runnable 可执行事件)到"消息队列中"，延时执行
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);        // 跳转到主页面
                startActivity(intent);
                finish();
            }
        }, 6000);

        //在此调用下面方法，才能捕获到线程中的异常
        Thread.setDefaultUncaughtExceptionHandler(this);

        checkExtra();
        initTypeXML("Type.xml");
        initSrc(Constant.CACHE_IMAGE_PATH,"cat_head.png");
        initSrc(Constant.CACHE_IMAGE_PATH,"test.png");
        initSrc(Constant.CACHE_IMAGE_PATH,"no_preview_picture.png");
        initSrc(Constant.IMAGE_PATH + "test","test.png");
        initSrc(Constant.IMAGE_PATH + "test","no_preview_picture.png");

        initView();
        initListener();
        initData();
        initType();
        initLogin();
        //copy();

    }

    /**
     * 检查挂载内存(内部内存)
     * 没有直接退出
     */
    private void checkExtra(){
        if(!MemorySizeUtils.externalMemoryAvailable()){
            ToastUtil.show("没有挂载＇内部内存＇");
            finish();
            System.exit(0);
        }
    }

    private void initView() {
        mTvCountDown = (TextView) findViewById(R.id.tv_count_down);
    }

    private void initListener(){

        mTvCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);        // 跳转到主页面
                startActivity(intent);
                finish();
                //如果之前创建了Runnable对象,那么就把这任务移除
                if(runnable!=null){
                    handler.removeCallbacks(runnable);
                }
            }
        });

    }

    private void initData() {
        myCountDownTimer = new MyCountDownTimer(6000, 1000);
        myCountDownTimer.start();
    }

    /**
     * 继承 CountDownTimer 防范
     *
     * 重写 父类的方法 onTick() 、 onFinish()
     */
    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数<br>
         * 例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法<br>
         * 例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        public void onFinish() {
            mTvCountDown.setTextSize(10);
            mTvCountDown.setText("正在跳转");
        }
        public void onTick(long millisUntilFinished) {
           // mTvCountDown.setText("倒计时(" + millisUntilFinished / 1000 + ")");
            mTvCountDown.setTextSize(25);
            mTvCountDown.setText(millisUntilFinished / 1000 +"");
        }
    }





    /**
     * 获取"消费类型"，添加到全局变量
     */
    private void initType(){
        PullTypeParser p = new PullTypeParser();
        try {
            p.parser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝数据库（xx.db）到files文件夹下
     * <br>注1：只在第一次打开应用才拷贝,第二次会判断有没有这个数据库
     * <br>注2：xx.db拷贝到工程目录assets目录下
     * <br>拿到files文件夹：File files = getFilesDir();
     * <br>如：复制后的data/data/com.shen.accountbook/files/xx.db"
     * @param dbName	数据库名称
     */
    private void initTypeXML(String dbName) {
        //1,在files文件夹下创建同名dbName数据库文件过程
        File files = getFilesDir();
        // 在files文件夹下生成一个"dbName名字"的文件
        File file = new File(files, dbName);

        // 如果"dbName名字的文件" 存在  （如第二次进入）
        if(file.exists()){
            return;
        }

        InputStream stream = null;
        FileOutputStream fos = null;

        //2,输入流读取assets目录下的文件
        try {
            // getAssets()拿到"资产目录"的文件夹（工程目录下的assets目录）
            // ***打开"dbName名字的文件"    （拿到他的输入流）
            stream = getAssets().open(dbName);

            //3,将读取的内容写入到指定文件夹的文件中去
            // ***拿到"file文件"的"输出流"
            fos = new FileOutputStream(file);

            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while( (temp = stream.read(bs))!=-1){	// 將"输入流"（stream）读到"bs"
                fos.write(bs, 0, temp);				// 將"bs"写到"fos"（输出流）
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(stream!=null && fos!=null){	// "流"非等于"null",说明没有关闭
                try {
                    // 关闭流
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制资源(assets目录下的文件)到指定路径
     * @param path          指定路径
     * @param imageName     图片文件名(文件名)
     */
    private void initSrc(String path, String imageName){
        File files = CreateFilesUtils.create(path);     // 创建"文件夹"
        File file = new File(files, imageName);
//        if(file.exists()){
//            return;
//        }

        InputStream stream = null;
        FileOutputStream fos = null;

        //2,输入流读取assets目录下的文件
        try {
            // getAssets()拿到"资产目录"的文件夹（工程目录下的assets目录）
            // ***打开"dbName名字的文件"    （拿到他的输入流）
            stream = getAssets().open(imageName);

            //3,将读取的内容写入到指定文件夹的文件中去
            // ***拿到"file文件"的"输出流"
            fos = new FileOutputStream(file);

            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while( (temp = stream.read(bs))!=-1){	// 將"输入流"（stream）读到"bs"
                fos.write(bs, 0, temp);				// 將"bs"写到"fos"（输出流）
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(stream!=null && fos!=null){	// "流"非等于"null",说明没有关闭
                try {
                    // 关闭流
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制
     */
    private void copy(){

        //  获取该程序的安装包路径
        String path=getApplicationContext().getPackageResourcePath();
        File file3 = this.getDatabasePath("AccountBook2.db").getParentFile();
        File file2 = this.getDatabasePath("AccountBook2.db");
        File file1 = new File("data/data/com.shen.accountbook2/databases/AccountBook2.db");
        LogUtils.i("file3:"+file3.getAbsolutePath());           // /data/data/com.shen.accountbook2/databases
        LogUtils.i("file2:"+file2.getAbsolutePath());           // /data/data/com.shen.accountbook2/databases/AccountBook2.db
        LogUtils.i("path:"+path);                               // /data/app/com.shen.accountbook2-1.apk

        if(!MemorySizeUtils.externalMemoryAvailable())                // 判断SDCard是否可用
            return;

        InputStream stream = null;
        FileOutputStream fos = null;

        //2,输入流读取assets目录下的文件
        try {
            // getAssets()拿到"资产目录"的文件夹（工程目录下的assets目录）
            // ***打开"dbName名字的文件"    （拿到他的输入流）
            stream = new FileInputStream(file2);

            //3,将读取的内容写入到指定文件夹的文件中去
            // ***拿到"file文件"的"输出流"
            fos = new FileOutputStream(Constant.CACHE_IMAGE_PATH+"AccountBook.db");

            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while( (temp = stream.read(bs))!=-1){	// 將"输入流"（stream）读到"bs"
                fos.write(bs, 0, temp);				// 將"bs"写到"fos"（输出流）
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(stream!=null && fos!=null){	// "流"非等于"null",说明没有关闭
                try {
                    // 关闭流
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 自动登录<p>
     * 自动登录 ＆＆ 记住密码
     */
    private void initLogin() {
        // 自动登录 ＆＆ 记住密码
        Boolean auto = SharePrefUtil.getBoolean(this, SharePrefUtil.KEY.AUTO_ISCHECK, false);
        Boolean remember = SharePrefUtil.getBoolean(this, SharePrefUtil.KEY.REMEMBER_ISCHECK, false);
        if(auto && remember){
            String mSp_user = SharePrefUtil.getString(this, SharePrefUtil.KEY.USENAME, "");
            String mSp_password = SharePrefUtil.getString(this, SharePrefUtil.KEY.PASSWORK, "");

            // System.out.println( "shen:"+mSp_user + ":" + mSp_password );
            // 从数据库查询
            mTableEx = new TableEx(this.getApplication());
            Cursor cursor = mTableEx.Query(Constant.TABLE_USER,new String[]{"name,password,sex,image,birthday,qq"}, "name=? and password=?",
                    new String[]{mSp_user,mSp_password},null,null,null);

            if(!cursor.moveToFirst() == false) {
                String c_name = cursor.getString(0);
                String c_password = cursor.getString(1);
                int c_sex = cursor.getInt(2);

                String c_image = cursor.getString(3);
                String c_birthday = cursor.getString(4);
                String c_qq = cursor.getString(5);

                // System.out.println( "shen:"+c_name + ":" + c_password + ":" + c_sex);
                if(mSp_user.equals(c_name) && mSp_password.equals(c_password)){
                    AccountBookApplication.setIsLogin(true);
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserName(c_name);
                    userInfo.setPassWord(c_password);
                    userInfo.setSex(c_sex);

                    userInfo.setImage(c_image);
                    userInfo.setBirthday(c_birthday);
                    userInfo.setQq(c_qq);

                    AccountBookApplication.setUserInfo(userInfo);

                    File files = CreateFilesUtils.create(Constant.IMAGE_PATH + c_name);     // 创建"用户文件夹"
                    if(files.exists())
                        LogUtils.i("SplashActivity当前用户文件夹"+files.getAbsolutePath());
                }
                else{
                    Toast.makeText(this,"自动登录失败：用户或密码错误", Toast.LENGTH_SHORT);
                    AccountBookApplication.setIsLogin(false);
                    AccountBookApplication.setUserInfo(null);
                }
            }else{
                AccountBookApplication.setIsLogin(false);
                AccountBookApplication.setUserInfo(null);
            }

            mTableEx.closeDBConnect();
            cursor.close();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)                 // 触摸事件
    {

//        if(event.getAction()==MotionEvent.ACTION_UP)
//        {
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            if (runnable != null)                           // 如果这个(runnable 可执行事件)被new出来了.
//                handler.removeCallbacks(runnable);          // 就从"消息队列"删除(这个事件)
//        }

        return super.onTouchEvent(event);
    }

    /************************************************************************************/
    // 按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"

            //如果之前创建了Runnable对象,那么就把这任务移除
            if(runnable!=null){
                handler.removeCallbacks(runnable);
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //在此处理异常， arg1即为捕获到的异常
        Log.i("AAA", "uncaughtException   " + ex);
    }
}
