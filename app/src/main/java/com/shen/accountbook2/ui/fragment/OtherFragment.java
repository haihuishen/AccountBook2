package com.shen.accountbook2.ui.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.AppUtils;
import com.shen.accountbook2.Utils.FileSizeUtils;
import com.shen.accountbook2.Utils.LogUtils;
import com.shen.accountbook2.Utils.MemorySizeUtils;
import com.shen.accountbook2.Utils.StreamUtil;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.ui.view.CommonProgressDialog;
import com.shen.accountbook2.ui.view.VersionUpdateDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Random;

/**
 * Created by shen on 9/9 0009.
 */
public class OtherFragment extends BaseFragment {

    protected static final String tag = "OtherFragment";


    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    /**
     * 更新新版本的状态码
     */
    protected static final int UPDATE_VERSION = 100;
    /**
     * 当前是最新版本
     */
    protected static final int LATEST_VERSION = 101;
    /**
     * url地址出错状态码
     */
    protected static final int URL_ERROR = 102;
    /**
     * IO出错状态码
     */
    protected static final int IO_ERROR = 103;
    /**
     * JSON出错状态码
     */
    protected static final int JSON_ERROR = 104;

    private String mNewVersionName;         // 新版本名称
    private String mNewVersionCode;            // 新版本号
    private String mVersionDes;             // 新版本描述
    private String mDownloadUrl;            // 新版本下载Url

    private String mCurrentVersionName;         // 当前版本名称
    private int mCurrentVersionCode;         // 当前版本号

    /******************************标题***********************************/
    private TextView tvTitle;


    private TextView mTvAddTest;             // 添加测试数据
    private TextView mTvCheckVersion;        // "检查版本"
    private TextView mTvVersionState;        // 弹出窗口"更新窗口"


    //    versionCode:主要是用于版本升级所用，是INT类型的，第一个版本定义为1，以后递增，这样只要判断该值就能确定是否需要升级，该值不显示给用户。
    //    versionName:这个是我们常说明的版本号，由三部分组成<major>.<minor>.<point>,该值是个字符串，可以显示给用户。
    //    关于版本号有两个值，如下面的例子
    //
    //    applicationId "com.xxx.xxx"
    //    versionCode  2
    //    versionName="1.1"
    //    ......
    //    versionCode是给设备程序识别版本(升级)用的，必须是一个interger值，整数，代表app更新过多少次
    //    versionName是给用户看的，可以写1.1 , 1.2等等版本
    private TextView mTvVersionCode;                        // 显示版本号
    private TextView mTvVersionName;                        // 显示版本名称
    private VersionUpdateDialog mVersionUpdateDialog;       // "更新窗口"对话框

    CommonProgressDialog mDialog;                               // 进度条对话框
    Thread thread;
    boolean mStop;                                              // 线程停止标志

    /******************************资源详情***********************************/
    private TextView mTvExternalMemorySize;                     // SD卡存储空间
    //private TextView mTvInternalMemorySize;                     // 内部存储空间
    private TextView mTvAppResourceLocation;                    // 本应用资源存放位置
    private TextView mTvAppResourceSize;                        // 资源占用
    private TextView mTvAppUserResourceLocation;                // 当前用户资源存放位置
    private TextView mTvAppUserResourceSize;                    // 资源占用


    public OtherFragment() {
    }


    private Handler mHandler = new Handler(){
        @Override
        //alt+ctrl+向下箭头,向下拷贝相同代码
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    mTvVersionState.setText("最新版本:"+ msg.obj);
                    //弹出对话框,提示用户更新
                    showVersionUpdateDialog(AppUtils.getVersionName(mContext),mNewVersionName,mVersionDes);
                    break;
                case LATEST_VERSION:
                    ToastUtil.show("当前是最新版本");
                    mTvVersionState.setText("当前是最新版本");
                    break;
                case URL_ERROR:
                    ToastUtil.show("url异常");
                    break;
                case IO_ERROR:
                    ToastUtil.show("读取异常");
                    break;
                case JSON_ERROR:
                    ToastUtil.show("json解析异常");
                    break;
            }
        }
    };

    @Override
    public View initUI() {
        View view = View.inflate(mContext, R.layout.fragment_other, null);

        /******************************标题***********************************/
        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        mTvAddTest = (TextView) view.findViewById(R.id.tv_addTest);
        mTvCheckVersion = (TextView) view.findViewById(R.id.tv_check_version);
        mTvVersionState = (TextView) view.findViewById(R.id.tv_version_state);

        mTvVersionCode = (TextView) view.findViewById(R.id.tv_versionCode);
        mTvVersionName = (TextView) view.findViewById(R.id.tv_versionName);

        /******************************资源详情***********************************/
        mTvExternalMemorySize = (TextView) view.findViewById(R.id.tv_External_Memory_Size);
        //mTvInternalMemorySize = (TextView) view.findViewById(R.id.tv_Internal_Memory_Size);
        mTvAppResourceLocation = (TextView) view.findViewById(R.id.tv_app_resource_location);
        mTvAppResourceSize = (TextView) view.findViewById(R.id.tv_app_resource_size);
        mTvAppUserResourceLocation = (TextView) view.findViewById(R.id.tv_app_user_resource_location);
        mTvAppUserResourceSize = (TextView) view.findViewById(R.id.tv_app_user_resource_size);


        return view;
    }

    @Override
    public void initListener() {
        mTvAddTest.setOnClickListener(this);
        mTvCheckVersion.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tvTitle.setText("其他");
        mStop = false;

        isPrepared = true;
        lazyLoad();

        checkVersion();
    }

    @Override
    protected void lazyLoad() {
        LogUtils.i("OtherFragment:========isPrepared:"+isPrepared+"=======isVisible:"+isVisible);
        if(!isPrepared || !isVisible) {
            return;
        }
        //填充各控件的数据
        mCurrentVersionCode = AppUtils.getVersionCode(mContext);
        mCurrentVersionName = AppUtils.getVersionName(mContext);

        //mTvVersionState.setText("");
        mTvVersionCode.setText("versionCode:  "+ mCurrentVersionCode);
        mTvVersionName.setText("versionName:  "+ mCurrentVersionName);

        /******************************资源详情***********************************/
        mTvExternalMemorySize.setText(MemorySizeUtils.formatFileSize(MemorySizeUtils.getAvailableExternalMemorySize(),false)
                +"/"+ MemorySizeUtils.formatFileSize(MemorySizeUtils.getTotalExternalMemorySize(),false));
        //mTvInternalMemorySize.setText(MemorySizeUtils.formatFileSize(MemorySizeUtils.getAvailableInternalMemorySize(),false)
        //        +"/"+ MemorySizeUtils.formatFileSize(MemorySizeUtils.getTotalInternalMemorySize(),false));

        mTvAppResourceLocation.setText(Constant.APP_RESOURCE_PATH);
        mTvAppResourceSize.setText(FileSizeUtils.getAutoFileOrFilesSize(Constant.APP_RESOURCE_PATH));

        if(AccountBookApplication.isLogin()) {
            mTvAppUserResourceLocation.setText(Constant.IMAGE_PATH + AccountBookApplication.getUserInfo().getUserName() + File.separator);
            mTvAppUserResourceSize.setText(
                    FileSizeUtils.getAutoFileOrFilesSize(Constant.IMAGE_PATH + AccountBookApplication.getUserInfo().getUserName() + File.separator));
        }else{
            mTvAppUserResourceLocation.setText("");
            mTvAppUserResourceSize.setText("");

        }
    }

    /**
     * 显示添加数据的窗口
     */
    private void showDialog(){
        mDialog = new CommonProgressDialog(getActivity()) {
            @Override
            public void cancel() {          // 点击取消按钮
                mDialog.dismiss();
                mStop = true;
            }
        };
        mDialog.setMessage("正在添加");
        mDialog.show();
    }


    /**
     * 弹出对话框,提示用户更新
     *
     */
    private void showVersionUpdateDialog(String currentVersionName, String newVersionName, String versionDescribe){
        // android.view.WindowManager$BadTokenException: Unable to add window — token null
        // 导致报这个错是在于new AlertDialog.Builder(mcontext)，虽然这里的参数是AlertDialog.Builder(Context context)
        // 但我们不能使用getApplicationContext()获得的Context,而必须使用Activity,因为只有一个Activity才能添加一个窗体。
        //
        // 解决方法：将new AlertDialog.Builder(Context context)中的参数用Activity.this（Activity是你的Activity的名称）
        // 来填充就可以正确的创建一个Dialog了。
        //对话框,是依赖于activity存在的
        mVersionUpdateDialog = new VersionUpdateDialog(getActivity()) {  // 注意这个上下文，用父的，还是自己的，全局的

            @Override
            public void up() {
                downloadApk();
                mVersionUpdateDialog.dismiss();
            }

            @Override
            public void cancel() {
                mVersionUpdateDialog.dismiss();
            }
        };

        mVersionUpdateDialog.setCurrentVersionName(currentVersionName);
        mVersionUpdateDialog.setNewVersionName(newVersionName);
        mVersionUpdateDialog.setVersionDescribe(mContext.getResources().getString(R.string.the_indentation) + versionDescribe);
        mVersionUpdateDialog.show();
    }



    /**
     * 检测版本号
     *  json中内容包含:
     * 更新版本的版本名称
     * 新版本的描述信息
     * 服务器版本号
     * 新版本apk下载地址
     */
    private void checkVersion() {
        new Thread(){
            public void run() {
                // 消息对象
                Message msg = Message.obtain();

//                // 开始时间
//                long startTime = System.currentTimeMillis();

                try {
                    //发送请求获取数据,参数则为请求json的链接地址
                    //http://192.168.23.1:8080/update.json	测试阶段不是最优
                    //10.0.2.2   仅限于模拟器访问电脑tomcat

                    //1,封装url地址               （记得添加网络权限）
                    URL url = new URL("http://192.168.23.1:8080/update.json");
                    //2,开启一个链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //3,设置常见请求参数(请求头)

                    //请求超时
                    connection.setConnectTimeout(2000);
                    //读取超时
                    connection.setReadTimeout(2000);

                    //默认就是"GET"请求方式
                    // connection.setRequestMethod("POST");

                    //4,获取请求成功响应码
                    if(connection.getResponseCode() == 200){
                        //5,以流的形式,将数据获取下来
                        InputStream is = connection.getInputStream();
                        //6,将流转换成字符串(工具类封装)
                        String json = StreamUtil.streamToString(is);
                        LogUtils.i(tag, json);
                        //7,json解析
                        JSONObject jsonObject = new JSONObject(json);

                        //debug调试,解决问题
                        //通过"JSONObject"解析 (字段要完全一致!!!)
                        mNewVersionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        mNewVersionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

                        //日志打印	;debug调试
                        LogUtils.i(tag, mNewVersionName);
                        LogUtils.i(tag, mVersionDes);
                        LogUtils.i(tag, mNewVersionCode);
                        LogUtils.i(tag, mDownloadUrl);

                        //8,比对版本号(服务器版本号>本地版本号,提示用户更新)
                        if(mCurrentVersionCode < Integer.parseInt(mNewVersionCode)){
                            //提示用户更新,弹出对话框(UI),消息机制
                            msg.what = UPDATE_VERSION;
                            msg.obj = mNewVersionName;
                        }else{
                            //进入应用程序主界面
                            msg.what = LATEST_VERSION;
                        }
                    }
                }catch(MalformedURLException e) {	// url地址出错状态码
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {			// IO出错状态码
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {			// JSON出错状态码
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally{
                    //指定睡眠时间,请求网络的时长超过4秒则不做处理
                    //请求网络的时长"小于4秒",强制让其睡眠满4秒钟
//                    long endTime = System.currentTimeMillis();
//
//                    if(endTime-startTime<4000){
//                        try {
//                            Thread.sleep(4000-(endTime-startTime));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }

                    //发送消息
                    mHandler.sendMessage(msg);
                }
            };
        }.start();

        //这种方法要new Runnable接口的子类,所以要实现接口没有实现的方法!
		/*new Thread(new Runnable() {
			@Override
			public void run() {

			}
		});*/
    }

    /**
     * 下载apk <p>
     * 使用了 xUtils
     */
    protected void downloadApk() {
        //apk下载链接地址,放置apk的所在路径

        //1,判断sd卡是否可用,是否挂在上
        if(MemorySizeUtils.externalMemoryAvailable()){
            //2,获取sd路径
            final String path = Constant.APP_RESOURCE_PATH+"test.apk";

            //3,发送请求,获取apk,并且放置到指定路径
            //httpUtils: com.lidroid.xutils.HttpUtils	(xUtils的!!!)
            HttpUtils httpUtils = new HttpUtils();

            //4,发送请求,传递参数(参数1：下载地址;参数2：下载应用放置位置;参数3：下载回调方法)
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {

                // 下载成功调用    （下载好的文件封装在 "responseInfo"中）
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    LogUtils.i(tag, "下载成功");
                    LogUtils.i(tag, "文件下载位置:"+path);
                    File file = responseInfo.result;//下载成功(下载过后的放置在sd卡中apk)
                    //提示用户安装
                    installApk(file);
                }

                // 下载失败调用
                @Override
                public void onFailure(HttpException error, String msg) {
                    LogUtils.i(tag, "下载失败");
                    //下载失败
                }

                //刚刚开始下载,调用该方法
                @Override
                public void onStart() {
                    LogUtils.i(tag, "刚刚开始下载");
                    super.onStart();
                }

                //下载过程中,调用该方法(下载apk总大小,当前的下载位置,是否正在下载)
                @Override
                public void onLoading(long total, long current,boolean isUploading) {
                    LogUtils.i(tag, "下载中........");
                    LogUtils.i(tag, "total = "+total);
                    LogUtils.i(tag, "current = "+current);
                    super.onLoading(total, current, isUploading);
                }
            });

        }
    }

    /**
     * 安装对应apk
     * @param file	安装文件
     */
    protected void installApk(File file) {
        //（安装apk的界面是）系统应用界面,源码,安装apk入口
        // 使用"隐式意图"
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
		/*//文件作为数据源
		intent.setData(Uri.fromFile(file));
		//设置安装的类型
		intent.setType("application/vnd.android.package-archive");*/
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        // startActivity(intent);
        startActivityForResult(intent, 0);
    }

    /**
     * 添加测试数据!
     */
    public void addTest(){

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final int MAINTTPE = 12;
                int TYPE1 = 0;
                int mt_Random = 0;
                int t1_Random = 0;
                int M = 1;
                int day = 1;
                int max = 0;
                int startYear = 2016;
                int endYear = 2018;
                int yearIndex = 900;           // 每年循环1800次，分摊到每一天
                int progress = 0;


                TableEx tableEx = new TableEx(AccountBookApplication.getContext());
                tableEx.Delete(Constant.TABLE_USER,"name=?",new String[]{"test"});
                tableEx.Delete(Constant.TABLE_CONSUMPTION,"user=?",new String[]{"test"});

//                String path = "data/data/com.shen.accountbook2/databases/AccountBook2.db";
//                SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,
//                        SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY );//读写、若不存在则创建

                // db.delete(Constant.TABLE_CONSUMPTION,"user=?",new String[]{"test"});

                // 添加"test用户"

                ContentValues userValues = new ContentValues();
                //   userValues.put("id", id);                    // 主键可以不写
                userValues.put("name", "test");                        // 字段  ： 值
                userValues.put("password", "123456");
                userValues.put("sex", 1);
                tableEx.Add(Constant.TABLE_USER, userValues);




                String[] mt ={"就餐","交通","零食","蒲","教育","生活用品","各种月费",
                        "家电","电子产品","交通工具","游戏","其他"};

                String[] ty0 ={"早餐","午餐","晚餐","夜宵","饭局","非正餐","其他"};
                String[] ty1 ={"船","火车","地铁","公交车","飞机","神州n号","的士","快车","其他"};
                String[] ty2 ={"饮料","酒","其他"};
                String[] ty3 ={"喝酒","唱K","其他"};
                String[] ty4 ={"学车","夜校","孩子学费","技能考试","其他"};
                String[] ty5 ={"洗头水","沐浴露","牙膏","洗衣粉","毛巾","其他"};
                String[] ty6 ={"仅房租","房租+水电","水费","电费","话费","其他"};
                String[] ty7 ={"电视","洗衣机","空调","其他"};
                String[] ty8 ={"手机","手表","其他"};
                String[] ty9 ={"自行车","摩托","其他"};
                String[] ty10 ={"其他"};
                String[] ty11 ={"其他"};

                Random ra =new Random();

                max = yearIndex * (endYear - startYear);        // 插入的总条数
                mDialog.setMax(max);

                for(int y = startYear; y<endYear; y++) {
                    for (int i = 0; i < yearIndex; i++) {
                        String type1 = null;
                        String maintype = null;
                        float price = 0;
                        int number = 0;
                        float unitPrice = 0;

                        mt_Random = ra.nextInt(MAINTTPE);
                        maintype = mt[mt_Random];

                        switch (mt_Random) {
                            case 0:
                                TYPE1 = 7;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty0[t1_Random];
                                break;
                            case 1:
                                TYPE1 = 9;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty1[t1_Random];
                                break;
                            case 2:
                                TYPE1 = 3;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty2[t1_Random];
                                break;
                            case 3:
                                TYPE1 = 3;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty3[t1_Random];
                                break;
                            case 4:
                                TYPE1 = 5;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty4[t1_Random];
                                break;
                            case 5:
                                TYPE1 = 6;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty5[t1_Random];
                                break;
                            case 6:
                                TYPE1 = 6;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty6[t1_Random];
                                break;
                            case 7:
                                TYPE1 = 4;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty7[t1_Random];
                                break;
                            case 8:
                                TYPE1 = 3;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty8[t1_Random];
                                break;
                            case 9:
                                TYPE1 = 3;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty9[t1_Random];
                                break;
                            case 10:
                                TYPE1 = 1;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty10[t1_Random];
                                break;
                            case 11:
                                TYPE1 = 1;
                                t1_Random = ra.nextInt(TYPE1);
                                type1 = ty11[t1_Random];
                                break;
                        }

                        number = ra.nextInt(10) + 1;                        // 数量
                        unitPrice = ra.nextFloat() * (100 - 1) + 1;         // 单价
                        price = number * unitPrice;                         // 总价

                        String id = null;   // 空不要写成 ""  ，不要写成 "null" 要写成String id = null;  主键可以不写
                        String concreteness = "自定义";
                        String image = null;    // 空不要写成 ""  ，不要写成 "null" 要写成String image = null;
                        String date = null;
                        image = "test.png";

                        NumberFormat nf = NumberFormat.getInstance();   // 得到一个NumberFormat的实例
                        nf.setGroupingUsed(false);                      // 设置是否使用分组
                        nf.setMaximumIntegerDigits(2);                  // 设置最大整数位数
                        nf.setMinimumIntegerDigits(2);                  // 设置最小整数位数

                        if (0 < M && M < 12) {
                            if (0 < day && day <= 28) {
                                date = y + "-" + nf.format(M) + "-" + nf.format(day);
                                day++;
                            } else {
                                M++;
                                day = 1;
                                date = y + "-" +nf.format(M) + "-" + nf.format(day);
                                day++;
                            }
                        } else {
                            M = 1;
                            if (0 < day && day <= 28) {
                                date = y + "-" + nf.format(M) + "-" + nf.format(day);
                                day++;
                            } else {
                                M++;
                                day = 1;
                                date = y + "-" + nf.format(M) + "-" + nf.format(day);
                                day++;
                            }
                        }

                        ContentValues values = new ContentValues();
                        //   values.put("id", id);                   // 主键可以不写
                        values.put("user", "test");                        // 字段  ： 值
                        values.put("maintype", maintype);                        // 字段  ： 值
                        values.put("type1", type1);
                        values.put("concreteness", concreteness);
                        values.put("price", ToFormatUtil.toDecimalFormat(price, 2));
                        values.put("number", number);
                        values.put("unitPrice", ToFormatUtil.toDecimalFormat(unitPrice, 2));
                        values.put("image", image);
                        values.put("date", date);   // 这里只要填写 YYYY-M-DD  ，不用填date(2016-9-12 00:00:00) 这么麻烦

                        tableEx.Add(Constant.TABLE_CONSUMPTION, values);
                        progress++;
                        mDialog.setProgress(progress);

                        if(mStop)               // 对话框点击"取消"就会将 mStop = true
                            i = yearIndex+1;    // 使用"最大值"来停止"进程"
                    }
                    if(mStop)                   // 对话框点击"取消"就会将 mStop = true
                        y = endYear+1;          // 使用"最大值"来停止"进程"
                }
                mDialog.dismiss();

            }
        });
        thread.start();
    }



    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_addTest:
                    showDialog();
                    mStop = false;
                    addTest();
                    break;

                case R.id.tv_check_version:
                    checkVersion();
                    break;

                default:
                    break;
            }

    }



}
