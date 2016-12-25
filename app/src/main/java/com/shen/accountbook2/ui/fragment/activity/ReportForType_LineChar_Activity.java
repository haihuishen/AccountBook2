package com.shen.accountbook2.ui.fragment.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.DateTimeFormat;
import com.shen.accountbook2.Utils.SharePrefUtil;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class ReportForType_LineChar_Activity extends FragmentActivity implements View.OnClickListener,PopupWindow.OnDismissListener {

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

    /** popwindows--曲线图设置*/
    PopupWindow pop;

    /******************************popwindows--年月选择***********************************/
    /** "年份"选择器*/
    TimePickerView pvTimeYear;
    /** "年月"选择器*/
    TimePickerView pvTimeYearMonth;
    //popwindows
    RelativeLayout mRlPopsNull;
    TextView mTvPopsCancel;
    TextView mTvPopsChoiceYear;
    TextView mTvPopsChoiceYearMonth;

    /** 选择"年份"还是"年月"*/
    PopupWindow pops;


    /******************************popwindowss--类型选择***********************************/
    /** "主类型"选择器*/
    OptionsPickerView pvOptionsMainType;
    /** "次类型"选择器*/
    OptionsPickerView pvOptionsType1;
    //popwindowss
    RelativeLayout mRlPopssNull;
    TextView mTvPopssCancel;
    TextView mTvPopssChoiceMainType;
    TextView mTvPopssChoiceType1;

    /** 选择"主类型"还是"次类型"*/
    PopupWindow popss;


    private TextView mTvChoiceYM;                   // 选择年月
    private TextView mTvChoiceType;               // 选择类型

    private int mTop = 100;                         // 坐标轴y轴

    private Button mBtnQuery;
    private Button mBtnClear;

    TableEx tableEx;

    PlaceholderFragment placeholderFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_type);

        mContext = this;

        boolean label = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_TYPE_ISCHECK,false);
        boolean fill = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_TYPE_ISCHECK,false);
        boolean select = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_TYPE_ISCHECK,false);
        int type = SharePrefUtil.getInt(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_TYPE_TYPE,2);

        placeholderFragment = PlaceholderFragment.newInstance(label, fill, select, type);
//        placeholderFragment = new PlaceholderFragment(label, fill, select, type);

        if (savedInstanceState == null) {
            // 拿到fragment管理器 , 将"PlaceholderFragment" new出来，提交给"R.id.container"
            getSupportFragmentManager().beginTransaction().add(R.id.container, placeholderFragment).commit();
        }


        initView();
        initListener();
        initDate();
        initialPopup();
        initialPopups();
        initialPopupss();
    }


    private void initView(){
        /******************************标题***********************************/
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mBtnMenu = (ImageButton) findViewById(R.id.btn_menu);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnSet = (ImageButton) findViewById(R.id.btn_title_set);

        // 选择年份
        mTvChoiceYM = (TextView) findViewById(R.id.tv_choiceYM);
        mTvChoiceType = (TextView) findViewById(R.id.tv_choiceType);

        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
    }

    private void initListener() {
        // 标题
        mTvTitle.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnSet.setOnClickListener(this);

        mTvChoiceYM.setOnClickListener(this);
        mTvChoiceType.setOnClickListener(this);

        mBtnQuery.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
    }

    private void initDate() {
        /******************************标题***********************************/
        mBtnMenu.setVisibility(View.GONE);
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnSet.setVisibility(View.VISIBLE);
        mTvTitle.setText("类型报表");


        tableEx = new TableEx(mContext);


        /******************************************************************************/
        //"年份"选择器
        pvTimeYear = new TimePickerView(this, TimePickerView.Type.YEAR);
        //控制时间范围
        //Calendar calendar = Calendar.getInstance();
        //pvTimeYear.setRange(calendar.get(Calendar.YEAR) - 116, calendar.get(Calendar.YEAR) + 50);//要在setTime 之前才有效果哦
        pvTimeYear.setTitle("年份");         // 设置"标题"
        pvTimeYear.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTimeYear.setCyclic(false);             // 是否循环滚动
        pvTimeYear.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTimeYear.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mTvChoiceYM.setText(DateTimeFormat.getTime(date,"yyyy"));
            }
        });

        /******************************************************************************/
        //"年月"选择器
        pvTimeYearMonth = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        //控制时间范围
        //Calendar calendar = Calendar.getInstance();
        //pvTimeYearMonth.setRange(calendar.get(Calendar.YEAR) - 116, calendar.get(Calendar.YEAR) + 50);//要在setTime 之前才有效果哦
        pvTimeYearMonth.setTitle("年月份");         // 设置"标题"
        pvTimeYearMonth.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTimeYearMonth.setCyclic(false);             // 是否循环滚动
        pvTimeYearMonth.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTimeYearMonth.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mTvChoiceYM.setText(DateTimeFormat.getTime(date,  "yyyy-MM"));
            }
        });


        /******************************************************************************/
        //"年份"选择器
        pvTimeYear = new TimePickerView(this, TimePickerView.Type.YEAR);
        //控制时间范围
        //Calendar calendar = Calendar.getInstance();
        //pvTimeYear.setRange(calendar.get(Calendar.YEAR) - 116, calendar.get(Calendar.YEAR) + 50);//要在setTime 之前才有效果哦
        pvTimeYear.setTitle("年份");         // 设置"标题"
        pvTimeYear.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTimeYear.setCyclic(false);             // 是否循环滚动
        pvTimeYear.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTimeYear.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mTvChoiceYM.setText(DateTimeFormat.getTime(date,"yyyy"));
            }
        });

        /******************************************************************************/
        //"年月"选择器
        pvTimeYearMonth = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        //控制时间范围
        //Calendar calendar = Calendar.getInstance();
        //pvTimeYearMonth.setRange(calendar.get(Calendar.YEAR) - 116, calendar.get(Calendar.YEAR) + 50);//要在setTime 之前才有效果哦
        pvTimeYearMonth.setTitle("年月份");         // 设置"标题"
        pvTimeYearMonth.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTimeYearMonth.setCyclic(false);             // 是否循环滚动
        pvTimeYearMonth.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTimeYearMonth.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mTvChoiceYM.setText(DateTimeFormat.getTime(date,  "yyyy-MM"));
            }
        });

        /******************************************************************************/
        //次类型选择器
        pvOptionsType1 = new OptionsPickerView(this);
        pvOptionsType1.setCancelable(false);     // true:点击弹出"布局"外部，收回"布局";false:没反应
        //三级联动效果
        pvOptionsType1.setPicker(AccountBookApplication.getMainTypeList(),
                AccountBookApplication.getListType1List(), null, true);
        //设置选择的三级单位
        // pwOptions.setLabels("主类型", "次类型", "次次类型");
        pvOptionsType1.setTitle("次类型");
        pvOptionsType1.setCyclic(false, false, true);         // 三级联动，哪个可以循环滚动

        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptionsType1.setSelectOptions(0, 0, 0);
        //选项选择器后回调
        pvOptionsType1.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = AccountBookApplication.getMainTypeList().get(options1)
                        +"-"+
                        AccountBookApplication.getListType1List().get(options1).get(option2);
                //     + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
                mTvChoiceType.setText(tx);
            }
        });

        /******************************************************************************/
        //主类型选择器
        pvOptionsMainType = new OptionsPickerView(this);
        pvOptionsMainType.setCancelable(false);     // true:点击弹出"布局"外部，收回"布局";false:没反应
        //三级联动效果
        pvOptionsMainType.setPicker(AccountBookApplication.getMainTypeList(),
                null, null, true);
        //设置选择的三级单位
        // pwOptions.setLabels("主类型", "次类型", "次次类型");
        pvOptionsMainType.setTitle("主类型");
        pvOptionsMainType.setCyclic(false, false, true);         // 三级联动，哪个可以循环滚动

        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptionsMainType.setSelectOptions(0, 0, 0);
        //选项选择器后回调
        pvOptionsMainType.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = AccountBookApplication.getMainTypeList().get(options1);
                //        +"-"+
                //        AccountBookApplication.getListType1List().get(options1).get(option2);
                //     + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
                mTvChoiceType.setText(tx);
            }
        });
    }

    /**
     * 初始化popupwindow
     * 曲线图的，设置
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

            switch (SharePrefUtil.getInt(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_TYPE_TYPE,2))
            {
                case 0: mPopRbX.setChecked(true); break;
                case 1: mPopRbY.setChecked(true); break;
                case 2: mPopRbYX.setChecked(true); break;
            }
            mPopCbToggleLabels.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_TYPE_ISCHECK,false));
            mPopCbToggleFilled.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_TYPE_ISCHECK,false));
            mPopCbSelectYX.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_TYPE_ISCHECK,false));

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
     * 初始化popupwindows
     * 选择年月
     */
    private void initialPopups() {

        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popupwindow_choice_year_month, null);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow对象
        pops = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        pops.setOnDismissListener(this);

        mRlPopsNull = (RelativeLayout) view.findViewById(R.id.layout_null);
        mTvPopsCancel = (TextView) view.findViewById(R.id.tv_choiceYM_cancel);
        mTvPopsChoiceYear = (TextView) view.findViewById(R.id.tv_year);
        mTvPopsChoiceYearMonth = (TextView) view.findViewById(R.id.tv_year_month);

        mRlPopsNull.setOnClickListener(this);
        mTvPopsCancel.setOnClickListener(this);
        mTvPopsChoiceYear.setOnClickListener(this);
        mTvPopsChoiceYearMonth.setOnClickListener(this);

        // 需要顺利让PopUpWindow dimiss（即点击PopuWindow之外的地方此或者back键PopuWindow会消失）；
        // PopUpWindow的背景不能为空。必须在popuWindow.showAsDropDown(v);
        // 或者其它的显示PopuWindow方法之前设置它的背景不为空：

        // 需要设置一下此参数，点击外边可消失
        pops.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pops.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pops.setFocusable(true);
    }

    /**
     * 显示popupwindows
     */
    private void showPopupWindows() {

        if (pops.isShowing()) {
            // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
            pops.dismiss();
        } else {
            // 显示窗口
            // pop.showAsDropDown(v);
            // 获取屏幕和PopupWindow的width和height
            pops.setAnimationStyle(R.style.MenuAnimationFade);           // 动画怎么设置，怎会动!
            pops.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            pops.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            // 设置显示PopupWindow的位置位于View的左下方，x,y表示坐标偏移量
            pops.showAsDropDown(mTvChoiceYM, 0, 0);                  // 绑定哪个控件来"控制"控件弹出  ???

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
        }
    }


    /**
     * 初始化popupwindowss
     * 选择类型
     */
    private void initialPopupss() {

        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popupwindow_choice_maintype_type1, null);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow对象
        popss = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popss.setOnDismissListener(this);

        mRlPopssNull = (RelativeLayout) view.findViewById(R.id.layout_type_null);
        mTvPopssCancel = (TextView) view.findViewById(R.id.tv_choiceType_cancel);
        mTvPopssChoiceMainType = (TextView) view.findViewById(R.id.tv_mainType);
        mTvPopssChoiceType1 = (TextView) view.findViewById(R.id.tv_type1);

        mRlPopssNull.setOnClickListener(this);
        mTvPopssCancel.setOnClickListener(this);
        mTvPopssChoiceMainType.setOnClickListener(this);
        mTvPopssChoiceType1.setOnClickListener(this);

        // 需要顺利让PopUpWindow dimiss（即点击PopuWindow之外的地方此或者back键PopuWindow会消失）；
        // PopUpWindow的背景不能为空。必须在popuWindow.showAsDropDown(v);
        // 或者其它的显示PopuWindow方法之前设置它的背景不为空：

        // 需要设置一下此参数，点击外边可消失
        popss.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        popss.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        popss.setFocusable(true);
    }

    /**
     * 显示popupwindowss
     */
    private void showPopupWindowss() {

        if (popss.isShowing()) {
            // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
            popss.dismiss();
        } else {
            // 显示窗口
            // pop.showAsDropDown(v);
            // 获取屏幕和PopupWindow的width和height
            popss.setAnimationStyle(R.style.MenuAnimationFade);           // 动画怎么设置，怎会动!
            popss.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            popss.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            // 设置显示PopupWindow的位置位于View的左下方，x,y表示坐标偏移量
            popss.showAsDropDown(mTvChoiceType, 0, 0);                  // 绑定哪个控件来"控制"控件弹出  ???

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
            SharePrefUtil.saveInt(getApplicationContext(), SharePrefUtil.REPORT_KEY.SELECTYX_TYPE_TYPE, 0);
            zoomType = ZoomType.HORIZONTAL;
        }
        if(mPopRbY.isChecked()) {
            SharePrefUtil.saveInt(getApplicationContext(), SharePrefUtil.REPORT_KEY.SELECTYX_TYPE_TYPE, 1);
            zoomType = ZoomType.VERTICAL;
        }
        if(mPopRbYX.isChecked()) {
            SharePrefUtil.saveInt(getApplicationContext(), SharePrefUtil.REPORT_KEY.SELECTYX_TYPE_TYPE, 2);
            zoomType = ZoomType.HORIZONTAL_AND_VERTICAL;
        }

        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_TYPE_ISCHECK,mPopCbToggleLabels.isChecked());
        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_TYPE_ISCHECK,mPopCbToggleFilled.isChecked());
        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_TYPE_ISCHECK,mPopCbSelectYX.isChecked());

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

        if(TextUtils.isEmpty(mTvChoiceYM.getText().toString())){
            ToastUtil.show("请选择年份");
            return;
        }
        if( TextUtils.isEmpty(mTvChoiceType.getText().toString())){
            ToastUtil.show("请选择类型");
            return;
        }
        boolean flag = true;        // 游标的记录数 < 月份的天数， 当循环大于就 false

        int maxNumberOfLines = 1;
        int[] numberOfPoints = new int[1];
        float[][] NumbersTab = new float[1][13];                      // 因为从0开始的，但是坐标我从1开始的所以最大32

        String date = mTvChoiceYM.getText().toString();
        String type = mTvChoiceType.getText().toString();
        String[] dates = date.split("-");
        String[] types = type.split("-");

        Calendar calendar = Calendar.getInstance();
        Cursor cursor = null;
        mTop = 100;

        /*年月*/
        if(dates.length == 1){                                // 年
            numberOfPoints[0] = 12 + 1;                     // 因为从0开始的，但是这里从 1开始，所以要多加一个点
            NumbersTab = new float[1][numberOfPoints[0]];

            if(types.length == 1){                                        // 主类型
                ToastUtil.show("types.length == 1");
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION,
                        new String[]{"sum(price),strftime('%Y-%m',date)"}, "date like ? and user=? and maintype=?",
                        new String[]{date + "%", AccountBookApplication.getUserInfo().getUserName(), type},
                        "strftime('%Y-%m',date)", null, null);

            }else if(types.length == 2) {                                 // 次类型
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION,
                        new String[]{"sum(price),strftime('%Y-%m',date)"}, "date like ? and user=? and maintype=? and type1=?",
                        new String[]{date + "%", AccountBookApplication.getUserInfo().getUserName(), types[0], types[1]},
                        "strftime('%Y-%m',date)", null, null);
            }
        }else if(dates.length == 2) {                         // 年月
            ToastUtil.show("dates.length == 2");
            try {
                calendar.setTime(new SimpleDateFormat("yyyy-MM").parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);      // 这个月多少天
            numberOfPoints[0] = dayOfMonth + 1;                     // 因为从0开始的，但是这里从 1开始，所以要多加一个点
            NumbersTab = new float[1][numberOfPoints[0]];

            if(types.length == 1){                                        // 主类型
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION,
                        new String[]{"sum(price),date"}, "date like ? and user=? and maintype=?",
                        new String[]{date + "%", AccountBookApplication.getUserInfo().getUserName(), type},
                        "date", null, null);

            }else if(types.length == 2) {                                 // 次类型
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION,
                        new String[]{"sum(price),date"}, "date like ? and user=? and maintype=? and type1=?",
                        new String[]{date + "%", AccountBookApplication.getUserInfo().getUserName(), types[0], types[1]},
                        "date", null, null);
            }
        }

        int num = cursor.getCount();
        try {
            if (num != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                cursor.moveToFirst();
                if (cursor.getString(0) != null) {        // 没记录，返回1，这里返回的是 "null"
                    if (Float.valueOf(cursor.getString(0)) >= 0) {
                        System.out.println("数量：" + num);

                        for (int i = 1; i < numberOfPoints[0]; i++) {
                            System.out.println("循环的:" + date + "-" + ToFormatUtil.toNumberFormat(i, 2));
                            if (flag) {
                                System.out.println("text:" + cursor.getString(1) + ":" + cursor.getString(0));
                                if ((date + "-" + ToFormatUtil.toNumberFormat(i, 2)).equals(cursor.getString(1))) {
                                    System.out.println("if:" + cursor.getString(1) + ":" + cursor.getString(0));
                                    NumbersTab[0][i] = cursor.getInt(0);
                                    if(mTop < cursor.getInt(0))
                                        mTop = cursor.getInt(0);
                                    if (!cursor.moveToNext()) {
                                        System.out.println("进来这里了!");
                                        flag = false;
                                    }
                                } else {
                                    NumbersTab[0][i] = 0;
                                    System.out.println("else:0");
                                }
                            } else {
                                NumbersTab[0][i] = 0;
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

        placeholderFragment.setMaxNumberOfLines(maxNumberOfLines);
        placeholderFragment.setNumberOfLines(maxNumberOfLines);
        placeholderFragment.setNumberOfPoints(numberOfPoints);
        placeholderFragment.setRandomNumbersTab(NumbersTab);
        placeholderFragment.generateData();
        placeholderFragment.resetViewport(setTop(mTop), numberOfPoints[0]-1);

    }

    /**
     * 清理
     */
    private void clear(){
        placeholderFragment.generateValues();
        placeholderFragment.generateData();
        placeholderFragment.resetViewport(100, 12);


        mTvChoiceYM.setText("");
        mTvChoiceType.setText("");
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
            case R.id.tv_choiceYM:                  // 弹出pop窗口，选择"年份"或"年月"
                showPopupWindows();
                break;

            case R.id.layout_null:                  // popupwindows上的"空白布局"
                if (pops != null) {
                    pops.dismiss();
                }
                break;

            case R.id.tv_year:                      // popupwindows上的"选择年份"
                if (pops != null) {
                    pops.dismiss();
                }
                pvTimeYear.show();
                break;

            case R.id.tv_year_month:                // popupwindows上的"选择年月"
                if (pops != null) {
                    pops.dismiss();
                }
                pvTimeYearMonth.show();
                break;

            case R.id.tv_choiceYM_cancel:            // popupwindows上的"选择年月"
                if (pops != null) {
                    pops.dismiss();
                }
                break;

            /****************************类型**************************************/
            case R.id.tv_choiceType:                            // 选择类型
                showPopupWindowss();
                break;

            case R.id.layout_type_null:                 // popupwindowss上的"空白布局"
                if (popss != null) {
                    popss.dismiss();
                }
                break;

            case R.id.tv_mainType:                      // popupwindowss上的"选择年份"
                if (popss != null) {
                    popss.dismiss();
                }
                pvOptionsMainType.show();
                break;

            case R.id.tv_type1:                         // popupwindowss上的"选择年月"
                if (popss != null) {
                    popss.dismiss();
                }
                pvOptionsType1.show();
                break;

            case R.id.tv_choiceType_cancel:            // popupwindowss上的"选择年月"
                if (popss != null) {
                    popss.dismiss();
                }
                break;

            /****************************查询、清空**************************************/
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

            if(pvTimeYear.isShowing()){
                pvTimeYear.dismiss();
                return true;
            }
            if(pvTimeYearMonth.isShowing()){
                pvTimeYearMonth.dismiss();
                return true;
            }
            if(pvOptionsMainType.isShowing()){
                pvOptionsMainType.dismiss();
                return true;
            }
            if(pvOptionsType1.isShowing()){
                pvOptionsType1.dismiss();
                return true;
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
            resetViewport(100, 12);

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
        public void resetViewport(int top, int right) {
            // Reset viewport height range to (0,100)
            // 重置窗口高度范围(0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = top;
            v.left = 1;
            v.right = right;
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
