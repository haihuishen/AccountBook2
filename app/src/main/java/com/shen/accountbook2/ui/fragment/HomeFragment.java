package com.shen.accountbook2.ui.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.ui.AddActivity;
import com.shen.accountbook2.ui.AddActivity2;
import com.shen.accountbook2.ui.fragment.activity.ReportForD2_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForD3_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForD4_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForD5_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForD_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForM2_LineChar_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForM_LineChar_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForType2_LineChar_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForType_LineChar_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForY2_LineChar_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForY_LineChar_Activity;
import com.shen.accountbook2.ui.view.CommonProgressDialog;

import java.text.NumberFormat;
import java.util.Random;

/**
 * Created by shen on 9/9 0009.
 */
public class HomeFragment extends BaseFragment{
    /******************************标题***********************************/
    private TextView tvTitle;

    private Button btn_report_day;
    private Button btn_report_month;
    private Button btn_report_month2;
    private Button btn_report_year;
    private Button btn_report_year2;
    private Button btn_report_type;
    private Button btn_report_type2;

    private Button btn_report_day2;
    private Button btn_report_day3;
    private Button btn_report_day4;
    private Button btn_report_day5;

    private Button mBtnAdd;                 // 添加按钮
    private Button mBtnAdd2;                 // 添加按钮
    private Button mBtnAddTest;             // 添加测试数据

    CommonProgressDialog mDialog;             // 进度条对话框
    Thread thread;

    boolean mStop;                          // 线程停止标志

    public HomeFragment() {
    }

    @Override
    public View initUI() {

        // 將 R.layout.base_pager布局 填充成 view,作为其布局
        View view = View.inflate(mContext, R.layout.fragment_home, null);

        /******************************标题***********************************/
        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        btn_report_day = (Button) view.findViewById(R.id.btn_D);
        btn_report_month = (Button) view.findViewById(R.id.btn_M);
        btn_report_month2 = (Button) view.findViewById(R.id.btn_M2);
        btn_report_year = (Button) view.findViewById(R.id.btn_Y);
        btn_report_year2 = (Button) view.findViewById(R.id.btn_Y2);
        btn_report_type = (Button) view.findViewById(R.id.btn_type);
        btn_report_type2 = (Button) view.findViewById(R.id.btn_type2);

        btn_report_day2 = (Button) view.findViewById(R.id.btn_D2);
        btn_report_day3 = (Button) view.findViewById(R.id.btn_D3);
        btn_report_day4 = (Button) view.findViewById(R.id.btn_D4);
        btn_report_day5 = (Button) view.findViewById(R.id.btn_D5);

        mBtnAdd = (Button) view.findViewById(R.id.btn_add);
        mBtnAdd2 = (Button) view.findViewById(R.id.btn_add2);
        mBtnAddTest = (Button) view.findViewById(R.id.btn_addTest);

        return view;
    }

    @Override
    public void initListener() {
        btn_report_day.setOnClickListener(this);
        btn_report_month.setOnClickListener(this);
        btn_report_month2.setOnClickListener(this);
        btn_report_year.setOnClickListener(this);
        btn_report_year2.setOnClickListener(this);
        btn_report_type.setOnClickListener(this);
        btn_report_type2.setOnClickListener(this);

        btn_report_day2.setOnClickListener(this);
        btn_report_day3.setOnClickListener(this);
        btn_report_day4.setOnClickListener(this);
        btn_report_day5.setOnClickListener(this);

        mBtnAdd.setOnClickListener(this);
        mBtnAdd2.setOnClickListener(this);
        mBtnAddTest.setOnClickListener(this);
    }


    @Override
    public void initData(){

        tvTitle.setText("首页");

        mStop = false;

    }

    private void showDialog(){
        mDialog = new CommonProgressDialog(getActivity()) {
            @Override
            public void cancel() {          // 点击取消按钮
                mDialog.dismiss();
                mStop = true;
            }
        };
        mDialog.setMessage("正在添加");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // 安卓弹出ProgressDialog进度框之后触摸屏幕就消失了的解决方法
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if(AccountBookApplication.isLogin()){
            switch (v.getId()){
                case R.id.btn_D:                       // 日报表
                    intent = new Intent(mContext, ReportForD_Activity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_D4:                       // 日报表
                    intent = new Intent(mContext, ReportForD4_Activity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_D5:                       // 日报表
                    intent = new Intent(mContext, ReportForD5_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_M:                       // 月报表
                    intent = new Intent(mContext, ReportForM_LineChar_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_M2:                       // 月报表2
                    intent = new Intent(mContext, ReportForM2_LineChar_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_Y:                       // 年报表
                    intent = new Intent(mContext, ReportForY_LineChar_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_Y2:                       // 年报表2
                    intent = new Intent(mContext, ReportForY2_LineChar_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_type:                    // 类型报表
                    intent = new Intent(mContext, ReportForType_LineChar_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_type2:                    // 类型报表2
                    intent = new Intent(mContext, ReportForType2_LineChar_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_D2:                     // 混合类报表
                    intent = new Intent(mContext, ReportForD2_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_D3:                     // 混合类报表
                    intent = new Intent(mContext, ReportForD3_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_add:                      // 添加"消费"
                    intent = new Intent(mContext, AddActivity.class);
                    // 解决在非Activity中使用startActivity;添加这个,如果要使用这种方式需要打开新的TASK。
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;

                case R.id.btn_add2:                      // 添加"消费2"
                    intent = new Intent(mContext, AddActivity2.class);
                    // 解决在非Activity中使用startActivity;添加这个,如果要使用这种方式需要打开新的TASK。
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;

                case R.id.btn_addTest:
                    showDialog();
                    mStop = false;
                    addTest();
                default:break;
            }
        }else
            ToastUtil.show("请登陆后再操作!");
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
                int startYear = 2012;
                int endYear = 2019;
                int yearIndex = 1800;           // 每年循环6000次，分摊到每一天
                int progress = 0;


                String path = "data/data/com.shen.accountbook2/databases/AccountBook2.db";
                SQLiteDatabase db = SQLiteDatabase.openDatabase(path,null,
                        SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY );//读写、若不存在则创建

                db.delete(Constant.TABLE_CONSUMPTION,"user=?",new String[]{"test"});

                // 添加"test用户"
                db.delete(Constant.TABLE_USER,"name=?",new String[]{"test"});
                ContentValues userValues = new ContentValues();
                //   userValues.put("id", id);                    // 主键可以不写
                userValues.put("name", "test");                        // 字段  ： 值
                userValues.put("password", "123");
                userValues.put("sex", 1);
                db.insert(Constant.TABLE_USER, "", userValues);

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

                        db.insert(Constant.TABLE_CONSUMPTION, "", values);
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

}
