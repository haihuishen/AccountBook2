package com.shen.accountbook2.ui.fragment.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.DateTimeFormat;
import com.shen.accountbook2.Utils.SharePrefUtil;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * Created by shen on 9/24 0024.
 */
public class ReportForY_LineChar_Activity extends FragmentActivity implements View.OnClickListener,PopupWindow.OnDismissListener {

    public static Context mContext;
    /******************************标题***********************************/
    private TextView mTvTitle;
    private ImageButton mBtnMenu;
    private ImageButton mBtnBack;
    private ImageButton mBtnSet;

    /****************************popwindow--选择"显示样式"********************************/
    private CheckBox mPopCbToggleLabels;
    private CheckBox mPopCbToggleFilled;

    private CheckBox mPopCbSelectYX;
    private RadioGroup mPopRgSelectYX;
    private RadioButton mPopRbYX;
    private RadioButton mPopRbY;
    private RadioButton mPopRbX;

    private Button mPopBtnConfirm;
    private Button mPopBtnCancel;

    /****************************popwindows--曲线图设置********************************/
    PopupWindow pop;

    /******************************年月选择***********************************/
    /** "年份"条件选择器*/
    TimePickerView pvTimeYear;

    private TextView mTvYear1;
    private TextView mTvYear2;
    private TextView mTvYear3;
    private TextView mTvYear4;
    private TextView mTvYear5;

    private boolean mBlM1;
    private boolean mBlM2;
    private boolean mBlM3;
    private boolean mBlM4;
    private boolean mBlM5;

    private int mCurrent;               // 当前是第几个
    private int mNum = 0;                   // 选择了几个月份
    private int mTop = 100;             // 坐标轴y轴

    private Button mBtnQuery;
    private Button mBtnClear;

    TableEx tableEx;

    PlaceholderFragment placeholderFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_year);

        mContext = this;

        boolean label = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_YEAR_ISCHECK,false);
        boolean fill = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_YEAR_ISCHECK,false);
        boolean select = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_ISCHECK,false);
        int type = SharePrefUtil.getInt(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_TYPE,2);

        placeholderFragment = PlaceholderFragment.newInstance(label, fill, select, type);
        // placeholderFragment = new PlaceholderFragment(label, fill, select, type);

        if (savedInstanceState == null) {
            // 拿到fragment管理器 , 将"PlaceholderFragment" new出来，提交给"R.id.container"
            getSupportFragmentManager().beginTransaction().add(R.id.container, placeholderFragment).commit();
        }


        initView();
        initListener();
        initDate();
        initialPopup();
    }


    private void initView(){
        /******************************标题***********************************/
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mBtnMenu = (ImageButton) findViewById(R.id.btn_menu);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnSet = (ImageButton) findViewById(R.id.btn_title_set);

        // 年份月份
        mTvYear1 = (TextView) findViewById(R.id.tv_year1);
        mTvYear2 = (TextView) findViewById(R.id.tv_year2);
        mTvYear3 = (TextView) findViewById(R.id.tv_year3);
        mTvYear4 = (TextView) findViewById(R.id.tv_year4);
        mTvYear5 = (TextView) findViewById(R.id.tv_year5);
        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
    }

    private void initListener() {
        // 标题
        mTvTitle.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnSet.setOnClickListener(this);

        // 年份月份
        mTvYear1.setOnClickListener(this);
        mTvYear2.setOnClickListener(this);
        mTvYear3.setOnClickListener(this);
        mTvYear4.setOnClickListener(this);
        mTvYear5.setOnClickListener(this);

        mBtnQuery.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
    }

    private void initDate() {
        /******************************标题***********************************/
        mBtnMenu.setVisibility(View.GONE);
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnSet.setVisibility(View.VISIBLE);
        mTvTitle.setText("年报表");

        /*******************************年份****************************************/
        mTvYear1.setEnabled(true);
        mTvYear2.setEnabled(false);
        mTvYear3.setEnabled(false);
        mTvYear4.setEnabled(false);
        mTvYear5.setEnabled(false);

        mTvYear1.setTextSize(20);
        mTvYear2.setTextSize(20);
        mTvYear3.setTextSize(20);
        mTvYear4.setTextSize(20);
        mTvYear5.setTextSize(20);

        tableEx = new TableEx(mContext);
        /*******************************年份****************************************/
        //时间选择器
        pvTimeYear = new TimePickerView(this, TimePickerView.Type.YEAR);
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        pvTimeYear.setRange(calendar.get(Calendar.YEAR) - 116, calendar.get(Calendar.YEAR) + 50);//要在setTime 之前才有效果哦
        pvTimeYear.setTitle("年份");         // 设置"标题"
        pvTimeYear.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTimeYear.setCyclic(false);             // 是否循环滚动
        pvTimeYear.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTimeYear.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {

                mBlM1 = mTvYear1.isEnabled();
                mBlM2 = mTvYear2.isEnabled();
                mBlM3 = mTvYear3.isEnabled();
                mBlM4 = mTvYear4.isEnabled();
                mBlM5 = mTvYear5.isEnabled();

                if(mBlM1 && !mBlM2 && !mBlM3 && !mBlM4 && !mBlM5 ) {
                    mNum = 1;
                    mTvYear2.setEnabled(true);
                    mTvYear1.setText(DateTimeFormat.getTime(date, "yyyy"));
                }
                if(mBlM1 && mBlM2 && !mBlM3 && !mBlM4 && !mBlM5 ) {
                    if(mCurrent == 1){
                        mTvYear1.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else {
                        mNum = 2;
                        mTvYear3.setEnabled(true);
                        mTvYear2.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }
                }
                if(mBlM1 && mBlM2 && mBlM3 && !mBlM4 && !mBlM5 ) {
                    if(mCurrent == 1){
                        mTvYear1.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else if(mCurrent == 2) {
                        mTvYear2.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else {
                        mNum = 3;
                        mTvYear4.setEnabled(true);
                        mTvYear3.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }
                }
                if(mBlM1 && mBlM2 && mBlM3 && mBlM4 && !mBlM5 ) {
                    if(mCurrent == 1){
                        mTvYear1.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else if(mCurrent == 2) {
                        mTvYear2.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else if(mCurrent == 3){
                        mTvYear3.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else {
                        mNum = 4;
                        mTvYear5.setEnabled(true);
                        mTvYear4.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }
                }
                if(mBlM1 && mBlM2 && mBlM3 && mBlM4 && mBlM5 ) {
                    if(mCurrent == 1){
                        mTvYear1.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else if(mCurrent == 2) {
                        mTvYear2.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else if(mCurrent == 3){
                        mTvYear3.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else if(mCurrent == 4){
                        mTvYear4.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }else{
                        mNum = 5;
                        mTvYear5.setText(DateTimeFormat.getTime(date, "yyyy"));
                    }
                }
            }
        });
    }

    /**
     * 初始化popupwindow
     */
    private void initialPopup() {

        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popupwindow_line_char_set, null);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow对象
        pop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        pop.setOnDismissListener(this);

        mPopCbToggleLabels = (CheckBox) view.findViewById(R.id.cb_toggleLabels);
        mPopCbToggleFilled = (CheckBox) view.findViewById(R.id.cb_toggleFilled);

        mPopCbSelectYX = (CheckBox) view.findViewById(R.id.cb_selectYX);
        mPopRgSelectYX = (RadioGroup) view.findViewById(R.id.rg_selectYX);
        mPopRbYX = (RadioButton) view.findViewById(R.id.rb_YX);
        mPopRbY = (RadioButton) view.findViewById(R.id.rb_Y);
        mPopRbX = (RadioButton) view.findViewById(R.id.rb_X);
        mPopBtnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        mPopBtnCancel = (Button) view.findViewById(R.id.btn_cancel);

        mPopBtnConfirm.setOnClickListener(this);
        mPopBtnCancel.setOnClickListener(this);

        // 需要顺利让PopUpWindow dimiss（即点击PopuWindow之外的地方此或者back键PopuWindow会消失）；
        // PopUpWindow的背景不能为空。必须在popuWindow.showAsDropDown(v);
        // 或者其它的显示PopuWindow方法之前设置它的背景不为空：

        // 需要设置一下此参数，点击外边可消失
        //pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        //pop.setOutsideTouchable(true);                // 不设置就没有
        // 设置此参数获得焦点，否则无法点击
        //pop.setFocusable(true);                       // 不设置就没有
    }

    /**
     * 显示popupwindow
     */
    private void showPopupWindow() {

        if (pop.isShowing()) {
            // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
            pop.dismiss();
        } else {

            switch (SharePrefUtil.getInt(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_TYPE,2))
            {
                case 0: mPopRbX.setChecked(true); break;
                case 1: mPopRbY.setChecked(true); break;
                case 2: mPopRbYX.setChecked(true); break;
            }
            mPopCbToggleLabels.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_YEAR_ISCHECK,false));
            mPopCbToggleFilled.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_YEAR_ISCHECK,false));
            mPopCbSelectYX.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_ISCHECK,false));

            // 显示窗口
            // pop.showAsDropDown(v);
            // 获取屏幕和PopupWindow的width和height
            pop.setAnimationStyle(R.style.MenuAnimationFade);           // 动画怎么设置，怎会动!
            // 设置了，就铺满窗口
            pop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            // pop.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            // 设置显示PopupWindow的位置位于View的左下方，x,y表示坐标偏移量
            pop.showAsDropDown(mTvTitle, 0, 20);           // 绑定哪个控件来"控制"控件弹出  ???

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
        }
    }

    /**
     * 保存样式
     */
    private void saveStyle(){
        ZoomType zoomType = null;
        if(mPopRbX.isChecked()) {
            SharePrefUtil.saveInt(getApplicationContext(), SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_TYPE, 0);
            zoomType = ZoomType.HORIZONTAL;
        }
        if(mPopRbY.isChecked()) {
            SharePrefUtil.saveInt(getApplicationContext(), SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_TYPE, 1);
            zoomType = ZoomType.VERTICAL;
        }
        if(mPopRbYX.isChecked()) {
            SharePrefUtil.saveInt(getApplicationContext(), SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_TYPE, 2);
            zoomType = ZoomType.HORIZONTAL_AND_VERTICAL;
        }

        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_YEAR_ISCHECK,mPopCbToggleLabels.isChecked());
        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_YEAR_ISCHECK,mPopCbToggleFilled.isChecked());
        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_ISCHECK,mPopCbSelectYX.isChecked());

        placeholderFragment.toggleLabels(mPopCbToggleLabels.isChecked());     // 在"节点(峰点)"中显示"文本(峰点值)"
        placeholderFragment.toggleFilled(mPopCbToggleFilled.isChecked());     // 区域的填充颜色(图形和坐标轴包围的区域)
        placeholderFragment.generateData();

        placeholderFragment.chart.setZoomEnabled(mPopCbSelectYX.isChecked()); // 是否允许，触摸放大
        placeholderFragment.chart.setZoomType(zoomType);

    }

    /**
     * 查询
     */
    private void query(){

        if(mNum == 0){
            ToastUtil.show("请选择年份");
            return;
        }
        boolean flag = true;        // 游标的记录数 < 月份的天数， 当循环大于就 false

        int maxNumberOfLines = mNum;
        int[] numberOfPoints = new int[mNum];
        float[][] NumbersTab = new float[mNum][13];                      // 因为从0开始的，但是坐标我从1开始的所以最大32

        String year1 = mTvYear1.getText().toString();
        String year2 = mTvYear2.getText().toString();
        String year3 = mTvYear3.getText().toString();
        String year4 = mTvYear4.getText().toString();
        String year5 = mTvYear5.getText().toString();

        ArrayList<String> yearList = new ArrayList<String>();
        yearList.add(year1);
        yearList.add(year2);
        yearList.add(year3);
        yearList.add(year4);
        yearList.add(year5);

        Cursor cursor = null;
        for (int i=0; i<mNum; i++) {
            flag = true;

            numberOfPoints[i] = 12 + 1;                     // 因为从0开始的，但是这里从 1开始，所以要多加一个点
            cursor = tableEx.Query(Constant.TABLE_CONSUMPTION,
                    new String[]{"sum(price),strftime('%Y-%m',date)"}, "date like ? and user=?",
                    new String[]{yearList.get(i) + "%",
                            AccountBookApplication.getUserInfo().getUserName()}, "strftime('%Y-%m',date)", null, null);

            int num = cursor.getCount();
            try {
                if (num != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                    cursor.moveToFirst();
                    if (cursor.getString(0) != null) {        // 没记录，返回1，这里返回的是 "null"
                        if (Float.valueOf(cursor.getString(0)) >= 0) {
                            System.out.println("数量：" + num);

                            for (int j = 1; j <= 12; j++) {
                                System.out.println("循环的:" + yearList.get(i) + "-" + ToFormatUtil.toNumberFormat(j, 2));
                                if (flag) {
                                    System.out.println("text:" + cursor.getString(1) + ":" + cursor.getString(0));
                                    if ((yearList.get(i) + "-" + ToFormatUtil.toNumberFormat(j, 2)).equals(cursor.getString(1))) {
                                        System.out.println("if:" + cursor.getString(1) + ":" + cursor.getString(0));
                                        NumbersTab[i][j] = cursor.getInt(0);
                                        if(mTop < cursor.getInt(0))
                                            mTop = cursor.getInt(0);
                                        if (!cursor.moveToNext()) {
                                            System.out.println("进来这里了!");
                                            flag = false;
                                        }
                                    } else {
                                        NumbersTab[i][j] = 0;
                                        System.out.println("else:0");
                                    }
                                } else {
                                    NumbersTab[i][j] = 0;
                                    System.out.println("flag:else:0");
                                }
                            }
                        } else {
                            // 不处理
                        }
                    } else {
                        // 不处理
                    }
                } else {
                    // 不处理
                }
            } catch (Exception e) {
                System.out.println("查询资产error:" + e.getMessage());
            }
        }
        placeholderFragment.setMaxNumberOfLines(maxNumberOfLines);
        placeholderFragment.setNumberOfLines(maxNumberOfLines);
        placeholderFragment.setNumberOfPoints(numberOfPoints);
        placeholderFragment.setRandomNumbersTab(NumbersTab);
        placeholderFragment.generateData();
        placeholderFragment.resetViewport(setTop(mTop));

    }

    /**
     * 清理
     */
    private void clear(){
        placeholderFragment.generateValues();
        placeholderFragment.generateData();
        placeholderFragment.resetViewport(100);

        mNum = 0;
        mTvYear1.setEnabled(true);
        mTvYear2.setEnabled(false);
        mTvYear3.setEnabled(false);
        mTvYear4.setEnabled(false);
        mTvYear5.setEnabled(false);
        mTvYear1.setText("");
        mTvYear2.setText("");
        mTvYear3.setText("");
        mTvYear4.setText("");
        mTvYear5.setText("");
    }

    /**
     * 设置y轴的最大值
     * @param top
     * @return
     */
    private int setTop(int top){

        if(0<=top && top<=100) return 100;
        else if(100<=top && top<=200) return 200;
        else if(200<=top && top<=300) return 300;
        else if(300<=top && top<=400) return 400;
        else if(400<=top && top<=500) return 500;
        else if(500<=top && top<=600) return 600;
        else if(600<=top && top<=700) return 700;
        else if(700<=top && top<=800) return 800;
        else if(800<=top && top<=900) return 900;
        else if(900<=top && top<=1000) return 1000;

        else if(1000<=top && top<=2000) return 2000;
        else if(2000<=top && top<=3000) return 3000;
        else if(3000<=top && top<=4000) return 4000;
        else if(4000<=top && top<=5000) return 5000;
        else if(5000<=top && top<=6000) return 6000;
        else if(6000<=top && top<=7000) return 7000;
        else if(7000<=top && top<=8000) return 8000;
        else if(8000<=top && top<=9000) return 9000;
        else if(9000<=top && top<=10000) return 10000;

        else if(10000<=top && top<=20000) return 20000;
        else if(20000<=top && top<=30000) return 30000;
        else if(30000<=top && top<=40000) return 40000;
        else if(40000<=top && top<=50000) return 50000;
        else if(50000<=top && top<=60000) return 60000;
        else if(60000<=top && top<=70000) return 70000;
        else if(70000<=top && top<=80000) return 80000;
        else if(80000<=top && top<=90000) return 90000;
        else if(90000<=top && top<=100000) return 100000;

        else if(100000<=top && top<=200000) return 200000;
        else if(200000<=top && top<=300000) return 300000;
        else if(300000<=top && top<=400000) return 400000;
        else if(400000<=top && top<=500000) return 500000;
        else if(500000<=top && top<=600000) return 600000;
        else if(600000<=top && top<=700000) return 700000;
        else if(700000<=top && top<=800000) return 800000;
        else if(800000<=top && top<=900000) return 900000;
        else if(900000<=top && top<=1000000) return 1000000;

        else if(1000000<=top && top<=2000000) return 2000000;
        else if(2000000<=top && top<=3000000) return 3000000;
        else if(3000000<=top && top<=4000000) return 4000000;
        else if(4000000<=top && top<=5000000) return 5000000;
        else if(5000000<=top && top<=6000000) return 6000000;
        else if(6000000<=top && top<=7000000) return 7000000;
        else if(7000000<=top && top<=8000000) return 8000000;
        else if(8000000<=top && top<=9000000) return 9000000;
        else if(9000000<=top && top<=10000000) return 10000000;

        else if(10000000<=top && top<=20000000) return 20000000;
        else if(20000000<=top && top<=30000000) return 30000000;
        else if(30000000<=top && top<=40000000) return 40000000;
        else if(40000000<=top && top<=50000000) return 50000000;
        else if(50000000<=top && top<=60000000) return 60000000;
        else if(60000000<=top && top<=70000000) return 70000000;
        else if(70000000<=top && top<=80000000) return 80000000;
        else if(80000000<=top && top<=90000000) return 90000000;
        else if(90000000<=top && top<=100000000) return 100000000;

        else
            return 100;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_back:                                         // 退出本Activity
                finish();
                break;

            case R.id.btn_title_set:                               // 点击"设置"
                showPopupWindow();
                ToastUtil.show("设置");
                break;

            /****************************popup_set**************************************/
            case R.id.btn_confirm:                               // 确认
                if (pop != null) {
                    pop.dismiss();
                }
                saveStyle();
                break;
            case R.id.btn_cancel:                               // 取消
                if (pop != null) {
                    pop.dismiss();
                }
                break;

            /****************************年份月份**************************************/
            case R.id.tv_year1:                               // 月份1
                mCurrent = 1;
                if (pvTimeYear != null) {
                    pvTimeYear.show();
                }
                break;
            case R.id.tv_year2:                               // 月份2
                mCurrent = 2;
                if (pvTimeYear != null) {
                    pvTimeYear.show();
                }
                break;
            case R.id.tv_year3:                               // 月份3
                mCurrent = 3;
                if (pvTimeYear != null) {
                    pvTimeYear.show();
                }
                break;
            case R.id.tv_year4:                               // 月份4
                mCurrent = 4;
                if (pvTimeYear != null) {
                    pvTimeYear.show();
                }
                break;
            case R.id.tv_year5:                               // 月份5
                mCurrent = 5;
                if (pvTimeYear != null) {
                    pvTimeYear.show();
                }
                break;
            case R.id.btn_query:                                 // 查询
                query();
                break;
            case R.id.btn_clear:                                // 清空
                clear();
                break;
        }
    }

    // 按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"

            if (pvTimeYear != null) {
                if(pvTimeYear.isShowing()){
                    pvTimeYear.dismiss();
                    return true;
                }
            }

            if (pop != null) {
                if(pop.isShowing()){
                    pop.dismiss();
                    return true;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDismiss() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1f;
        getWindow().setAttributes(params);
    }

    /******************************************************************************************************/
    /******************************************************************************************************/
    /**
     * A fragment containing a line chart.
     * 填充成"fragment"的类
     */
    public static class PlaceholderFragment extends Fragment {

        private LineChartView chart;
        private LineChartData data;
        /**
         * 添加了第几条线(用于在数组中找到画哪一条线)
         */
        private int numberOfLines = 1;

        /**
         * 最多4条线
         */
        private int maxNumberOfLines = 1;

        /**
         * 每一条线12个点
         */
        private int[] numberOfPoints = new int[]{31};

        /**
         * 这个应该是放置随机数的!
         */
        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints[0]];

        //Axes ['æksiːz]  轴线;轴心;坐标轴
        //Shape  [ʃeɪp] 形状
        //Point [pɒɪnt] 点
        //Cubic ['kjuːbɪk] 立方体;立方的
        //Label ['leɪb(ə)l] 标签

        /**
         * 显示/隐藏"坐标轴" true:显示坐标轴/false:隐藏坐标轴
         */
        private boolean hasAxes = true;
        /**
         * 显示/隐藏"坐标轴名称" true:显示/false:隐藏
         */
        private boolean hasAxesNames = false;
        /**
         * true:连线  false：不连线(只有点)
         */
        private boolean hasLines = true;
        /**
         * true:显示"节点(峰)"  false:去掉"节点(峰)"--就不可点击了
         */
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        /**
         * 区域的填充颜色(图形和坐标轴包围的区域) false:不填充/true:填充
         */
        private boolean isFilled = false;
        /**
         * 在"节点(峰点)"中显示"文本(峰点值)" false:不显示/true:显示
         */
        private boolean hasLabels = false;
        /**
         * false:折线/true:滑线
         */
        private boolean isCubic = false;
        /**
         * 点击"节点(峰点)"时，是否有"节点标签" true:点击时显示"峰点标签值"/ false:点击时不显示"峰点标签值"
         */
        private boolean hasLabelForSelected = false;
        /**
         * 让"节点(峰点)"变色--变一种
         */
        private boolean pointsHaveDifferentColor;

        private boolean isZoom = false;     // 是否可以触摸放大
        private ZoomType zoomType = ZoomType.HORIZONTAL_AND_VERTICAL;

        public PlaceholderFragment() {
        }

//        /**
//         *
//         * @param showLable     在"节点(峰点)"中显示"文本(峰点值)" false:不显示/true:显示
//         * @param isFill        区域的填充颜色(图形和坐标轴包围的区域) false:不填充/true:填充
//         * @param isZoom        是否可以触摸放大
//         * @param zoomType      触摸放大类型
//         */
//        public PlaceholderFragment(boolean showLable, boolean isFill, boolean isZoom, int zoomType) {
//            hasLabels = showLable;
//            isFilled = isFill;
//            this.isZoom = isZoom;
//
//            if(zoomType == 0)this.zoomType = ZoomType.HORIZONTAL;
//            else if(zoomType == 1)this.zoomType = ZoomType.VERTICAL;
//            else if(zoomType == 2)this.zoomType = ZoomType.HORIZONTAL_AND_VERTICAL;
//
//        }

        public static PlaceholderFragment newInstance(boolean showLable, boolean isFill, boolean isZoom, int zoomType) {
            PlaceholderFragment newFragment = new PlaceholderFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("showLable", showLable);
            bundle.putBoolean("isFill", isFill);
            bundle.putBoolean("isZoom", isZoom);
            bundle.putInt("zoomType", zoomType);

            newFragment.setArguments(bundle);
            return newFragment;

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle args = getArguments();
            if (args != null) {

                hasLabels = args.getBoolean("showLable");
                isFilled = args.getBoolean("isFill");
                isZoom = args.getBoolean("isZoom");
                int zoomType = args.getInt("zoomType");

                if(zoomType == 0)this.zoomType = ZoomType.HORIZONTAL;
                else if(zoomType == 1)this.zoomType = ZoomType.VERTICAL;
                else if(zoomType == 2)this.zoomType = ZoomType.HORIZONTAL_AND_VERTICAL;
            }
        }


        public int[] getNumberOfPoints() {
            return numberOfPoints;
        }

        public void setNumberOfPoints(int[] numberOfPoints) {
            this.numberOfPoints = numberOfPoints;
        }

        public int getMaxNumberOfLines() {
            return maxNumberOfLines;
        }

        public void setMaxNumberOfLines(int maxNumberOfLines) {
            this.maxNumberOfLines = maxNumberOfLines;
        }

        public int getNumberOfLines() {
            return numberOfLines;
        }

        public void setNumberOfLines(int numberOfLines) {
            this.numberOfLines = numberOfLines;
        }

        public float[][] getRandomNumbersTab() {
            return randomNumbersTab;
        }

        public void setRandomNumbersTab(float[][] randomNumbersTab) {
            this.randomNumbersTab = randomNumbersTab;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            //hasMenu——如果这是真的,显示fragment菜单项
            setHasOptionsMenu(true);
            //第一个参数传入布局的资源ID，生成fragment视图，
            // 第二个参数是视图的父视图，通常我们需要父视图来正确配置组件。
            // 第三个参数告知布局生成器是否将生成的视图添加给父视图。
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            // 这个应该是"点击峰点"
            // 实现一下我们实例的"接口中的某个方法"(内容根据我们需要的写)
            chart.setOnValueTouchListener(new ValueTouchListener());

            // Generate some random values. --生成一些值
            generateValues();
            // 根据值，画点，处理生成"图表"--全局变量：chart
            generateData();

            // Disable viewport recalculations, see toggleCubic() method for more info.
            // 禁用窗口重新计算,看到toggleCubic更多信息()方法。

            chart.setViewportCalculationEnabled(false);
            resetViewport(100);

            chart.setZoomEnabled(isZoom); // 是否允许，触摸放大
            chart.setZoomType(zoomType);

            return rootView;

        }

        /**
         * 让曲线图区空白<p>
         *
         */
        private void generateValues() {
            maxNumberOfLines = 0;               //  让曲线图区空白
            numberOfLines = 0;                  //  让曲线图区空白
        }

        /***
         * 根据值，画点，处理生成"图表"--全局变量：chart
         */
        public void generateData() {

            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < numberOfLines; ++i) {
                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < numberOfPoints[i]; ++j) {
                    values.add(new PointValue(j, randomNumbersTab[i][j]));
                }
                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
                if (pointsHaveDifferentColor) {
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }
            data = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("X:日");
                    axisY.setName("Y:￥");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);
        }

        /**
         * 重置窗口高度范围(0,100)
         */
        public void resetViewport(int top) {
            // Reset viewport height range to (0,100)
            // 重置窗口高度范围(0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = top;
            v.left = 1;
            v.right = 12;
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);

        }


        /** 在"节点(峰点)"中显示"文本(峰点值)"*/
        private void toggleLabels(boolean isClick) {
            hasLabels = isClick;
            if (hasLabels) {
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);
            }
            //generateData();
        }

        /** 区域的填充颜色(图形和坐标轴包围的区域)*/
        private void toggleFilled(boolean isClick) {
            isFilled = isClick;
            //generateData();
        }


        /**
         * 点击"峰点"后
         */
        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }
        }
    }

}
