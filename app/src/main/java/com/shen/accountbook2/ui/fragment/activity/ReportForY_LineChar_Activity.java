package com.shen.accountbook2.ui.fragment.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.SharePrefUtil;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.clander.SpecialCalendar;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.widget.OnWheelChangedListener;
import com.shen.accountbook2.widget.WheelView;
import com.shen.accountbook2.widget.adapters.ArrayWheelAdapter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class ReportForY_LineChar_Activity extends FragmentActivity implements View.OnClickListener,OnWheelChangedListener {

    PlaceholderFragment placeholderFragment;

    private TextView tv_top_year;           // 顶部显示"年份"的 文本
    private ImageView iv_back;              // 返回
    private ImageView iv_menu;              // 显示菜单

    private TextView lineYear1;
    private TextView lineYear2;
    private TextView lineYear3;
    private TextView lineYear4;
    private TextView lineYear5;

    private List<TextView> list_tv;

    private Date date;
    private SimpleDateFormat sdf;
    private String currentDate;
    private String c_year;
    private String c_month;

    /****************************对话框的--选择"年"********************************/
    private AlertDialog.Builder dialog_Y; // 年月选择对话框
    private View dialogLayout_Y;

    private RadioGroup rg_year;
    private RadioButton rb_1year;
    private RadioButton rb_5years;
    private RadioButton rb_5years_continuous;

    private WheelView wv_year;
    private WheelView wv_5years_start;     // 连续5年
    private WheelView wv_5years_end;
    private WheelView wv_5years_1;
    private WheelView wv_5years_2;
    private WheelView wv_5years_3;
    private WheelView wv_5years_4;
    private WheelView wv_5years_5;

    private CheckBox cb_year1;
    private CheckBox cb_year2;
    private CheckBox cb_year3;
    private CheckBox cb_year4;
    private CheckBox cb_year5;

    /****************************对话框的--选择"显示样式"********************************/
    private AlertDialog.Builder dialog_SHOW; // 样式选择对话框
    private View dialogLayout_SHOW;
    private CheckBox cb_toggleLabels;
    private CheckBox cb_toggleFilled;

    private CheckBox cb_selectYX;
    private RadioGroup rg_selectYX;
    private RadioButton rb_YX;
    private RadioButton rb_Y;
    private RadioButton rb_X;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        boolean label = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_YEAR_ISCHECK,false);
        boolean fill = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_YEAR_ISCHECK,false);
        boolean select = SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_ISCHECK,false);
        int type = SharePrefUtil.getInt(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_TYPE,2);

        placeholderFragment = new PlaceholderFragment(label, fill, select, type);

        if (savedInstanceState == null) {
            // 拿到fragment管理器 , 将"PlaceholderFragment" new出来，提交给"R.id.container"
            getSupportFragmentManager().beginTransaction().add(R.id.container, placeholderFragment).commit();
        }

        initUI();
        iniData();
        initListener();

        initAlertDialog_YearAndMonth();
        initAlertDialog_SHOW();
    }

    /** 更新UI*/
    private void initUI() {
        tv_top_year = (TextView) findViewById(R.id.tv_year);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);

        lineYear1 = (TextView) findViewById(R.id.tv_month1);
        lineYear2 = (TextView) findViewById(R.id.tv_month2);
        lineYear3 = (TextView) findViewById(R.id.tv_month3);
        lineYear4 = (TextView) findViewById(R.id.tv_month4);
        lineYear5 = (TextView) findViewById(R.id.tv_month5);

        list_tv = new ArrayList<TextView>();
        list_tv.add(lineYear1);
        list_tv.add(lineYear2);
        list_tv.add(lineYear3);
        list_tv.add(lineYear4);
        list_tv.add(lineYear5);

    }

    /**
     * 初始化"监听"
     */
    private void initListener(){
        tv_top_year.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
    }

    /**
     * 初始化"数据"
     */
    private void iniData(){
        date = new Date();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(date);//当期日期
        c_year = currentDate.split("-")[0];

        tv_top_year.setText("年");
        list_tv.get(0).setText(c_year + "年");
        list_tv.get(0).setTextColor(ChartUtils.COLORS[0]);

    }


    /**
     * 初始化Dialog——选择年月
     */
    private void initAlertDialog_YearAndMonth(){
        // 拿到当前"系统年份"在Constant.dialogYear数组中的"索引"
        int index_year = 0;
        for (index_year = 0; index_year < Constant.dialogYear.length; index_year++) {
            if (c_year.equals(Constant.dialogYear[index_year]))
                break;
        }

        //        public View inflate(int Resourece,ViewGroup root)
        //        作用：填充一个新的视图层次结构从指定的XML资源文件中
        //        Resource：View的layout的ID
        //        root： 生成的层次结构的根视图
        //        return 填充的层次结构的根视图。如果参数root提供了，那么root就是根视图；否则填充的XML文件的根就是根视图。
        //        其余几个重载的inflate函数类似。
        LayoutInflater inflater = getLayoutInflater();
        dialogLayout_Y = inflater.inflate(R.layout.dialog_select_y,(ViewGroup) findViewById(R.id.dialog));

        rg_year = (RadioGroup) dialogLayout_Y.findViewById(R.id.rg_year);
        rb_1year = (RadioButton) dialogLayout_Y.findViewById(R.id.rb_1year);
        rb_5years = (RadioButton) dialogLayout_Y.findViewById(R.id.rb_5years);
        rb_5years_continuous = (RadioButton) dialogLayout_Y.findViewById(R.id.rb_5years_continuous);

        wv_year = (WheelView) dialogLayout_Y.findViewById(R.id.wv_year);
        wv_5years_start = (WheelView) dialogLayout_Y.findViewById(R.id.wv_5years_start);
        wv_5years_end = (WheelView) dialogLayout_Y.findViewById(R.id.wv_5years_end);
        wv_5years_1 = (WheelView) dialogLayout_Y.findViewById(R.id.wv_5years_1);
        wv_5years_2 = (WheelView) dialogLayout_Y.findViewById(R.id.wv_5years_2);
        wv_5years_3 = (WheelView) dialogLayout_Y.findViewById(R.id.wv_5years_3);
        wv_5years_4 = (WheelView) dialogLayout_Y.findViewById(R.id.wv_5years_4);
        wv_5years_5 = (WheelView) dialogLayout_Y.findViewById(R.id.wv_5years_5);

        cb_year1 = (CheckBox) dialogLayout_Y.findViewById(R.id.cb_year1);
        cb_year2 = (CheckBox) dialogLayout_Y.findViewById(R.id.cb_year2);
        cb_year3 = (CheckBox) dialogLayout_Y.findViewById(R.id.cb_year3);
        cb_year4 = (CheckBox) dialogLayout_Y.findViewById(R.id.cb_year4);
        cb_year5 = (CheckBox) dialogLayout_Y.findViewById(R.id.cb_year5);


        // 年份滚动器
        ArrayWheelAdapter<String> yearAdapter = new ArrayWheelAdapter<String>(this, Constant.dialogYear);
        yearAdapter.setTextSize(30);                // 文本(字体)大小
        wv_year.setViewAdapter(yearAdapter);      // 添加适配器
        wv_year.setVisibleItems(1);               // 可见数目，默认是5
        wv_year.setCurrentItem(index_year);

        // 年份滚动器--连续五年的开始
        ArrayWheelAdapter<String> yearAdapter_start = new ArrayWheelAdapter<String>(this, Constant.dialogYear);
        yearAdapter_start.setTextSize(15);                // 文本(字体)大小
        wv_5years_start.setViewAdapter(yearAdapter_start);      // 添加适配器
        wv_5years_start.setVisibleItems(3);               // 可见数目，默认是5
        // 添加change事件
        wv_5years_start.addChangingListener(this);
        wv_5years_start.setCurrentItem(index_year);

        wv_5years_end.setVisibleItems(3);               // 可见数目，默认是5

        // 年份滚动器
        ArrayWheelAdapter<String> yearAdapter_1 = new ArrayWheelAdapter<String>(this, Constant.dialogYear);
        yearAdapter_1.setTextSize(20);                // 文本(字体)大小
        wv_5years_1.setViewAdapter(yearAdapter_1);      // 添加适配器
        wv_5years_1.setVisibleItems(1);               // 可见数目，默认是5
        wv_5years_1.setCurrentItem(index_year);
        // 年份滚动器
        ArrayWheelAdapter<String> yearAdapter_2 = new ArrayWheelAdapter<String>(this, Constant.dialogYear);
        yearAdapter_2.setTextSize(20);                // 文本(字体)大小
        wv_5years_2.setViewAdapter(yearAdapter_2);      // 添加适配器
        wv_5years_2.setVisibleItems(1);               // 可见数目，默认是5
        wv_5years_2.setCurrentItem(index_year);
        // 年份滚动器
        ArrayWheelAdapter<String> yearAdapter_3 = new ArrayWheelAdapter<String>(this, Constant.dialogYear);
        yearAdapter_3.setTextSize(20);                // 文本(字体)大小
        wv_5years_3.setViewAdapter(yearAdapter_3);      // 添加适配器
        wv_5years_3.setVisibleItems(1);               // 可见数目，默认是5
        wv_5years_3.setCurrentItem(index_year);
        // 年份滚动器
        ArrayWheelAdapter<String> yearAdapter_4 = new ArrayWheelAdapter<String>(this, Constant.dialogYear);
        yearAdapter_4.setTextSize(20);                // 文本(字体)大小
        wv_5years_4.setViewAdapter(yearAdapter_4);      // 添加适配器
        wv_5years_4.setVisibleItems(1);               // 可见数目，默认是5
        wv_5years_4.setCurrentItem(index_year);
        // 年份滚动器
        ArrayWheelAdapter<String> yearAdapter_5 = new ArrayWheelAdapter<String>(this, Constant.dialogYear);
        yearAdapter_5.setTextSize(20);                // 文本(字体)大小
        wv_5years_5.setViewAdapter(yearAdapter_5);      // 添加适配器
        wv_5years_5.setVisibleItems(1);               // 可见数目，默认是5
        wv_5years_5.setCurrentItem(index_year);

        rb_1year.setChecked(true);      // 默认选中

        //  初始化"对话框"——选择年
        dialog_Y = new AlertDialog.Builder(this).setTitle("选择年").setView(dialogLayout_Y)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean flag = true;
                        String[] year = new String[]{"2016"};
                        if(rb_1year.isChecked()){
                            flag = true;
                            year = new String[1];
                            year[0] = Constant.dialogYear[wv_year.getCurrentItem()];
                        }else if(rb_5years_continuous.isChecked()){
                            flag = true;
                            // start:2016  end:2017 ==> wv.getCurrentItem()==0，而这是两条线!!
                            int num_line = wv_5years_end.getCurrentItem()+2;
                            year = new String[num_line];
                            for(int i=0; i<num_line; i++){  // 这样取年份就不用怕 end的越界了(在Constant.dialogYear中)
                                year[i] = (Integer.valueOf(Constant.dialogYear[wv_5years_start.getCurrentItem()])+i)+"";
                            }
                        }else if(rb_5years.isChecked()){
                            List<String> years = new ArrayList<String>();
                            int num = 0;
                            if(cb_year1.isChecked()) {num++;years.add(Constant.dialogYear[wv_5years_1.getCurrentItem()]);}
                            if(cb_year2.isChecked()) {num++;years.add(Constant.dialogYear[wv_5years_2.getCurrentItem()]);}
                            if(cb_year3.isChecked()) {num++;years.add(Constant.dialogYear[wv_5years_3.getCurrentItem()]);}
                            if(cb_year4.isChecked()) {num++;years.add(Constant.dialogYear[wv_5years_4.getCurrentItem()]);}
                            if(cb_year5.isChecked()) {num++;years.add(Constant.dialogYear[wv_5years_5.getCurrentItem()]);}

                            if(years.size()==0) {
                               flag = false;
                            }else {
                                flag = true;
                                year = new String[years.size()];    // 这个new 很重要
                                years.toArray(year);                // 不要用成  year = (String[]) years.toArray();
                            }
                        }

                        if(flag){
                            try {
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                field.setAccessible(true);
                                //设置mShowing值，欺骗android系统
                                field.set(dialog, true);  //需要关闭的时候 将这个参数设置为true 他就会自动关闭了
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < list_tv.size(); i++) {     // 将月份控件填上内容，和颜色
                                if (year.length > i) {
                                    list_tv.get(i).setText(year[i] + "年");
                                    list_tv.get(i).setTextColor(ChartUtils.COLORS[i]);
                                } else {
                                    list_tv.get(i).setText("");             // 5个月份(少于5个的，其他的填"")
                                }
                            }
                            upViewData(year);


                        }else{
                            try {
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                field.setAccessible(true);
                                //设置mShowing值，欺骗android系统
                                field.set(dialog, false);  //需要关闭的时候 将这个参数设置为true 他就会自动关闭了
                                Toast.makeText(getApplicationContext(), "请选择年份!", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            //设置mShowing值，欺骗android系统
                            field.set(dialog, true);  //需要关闭的时候 将这个参数设置为true 他就会自动关闭了
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 初始化Dialog——选择显示样式
     */
    private void initAlertDialog_SHOW(){


        //        public View inflate(int Resourece,ViewGroup root)
        //        作用：填充一个新的视图层次结构从指定的XML资源文件中
        //        Resource：View的layout的ID
        //        root： 生成的层次结构的根视图
        //        return 填充的层次结构的根视图。如果参数root提供了，那么root就是根视图；否则填充的XML文件的根就是根视图。
        //        其余几个重载的inflate函数类似。
        LayoutInflater inflater = getLayoutInflater();
        dialogLayout_SHOW = inflater.inflate(R.layout.dialog_select_show,(ViewGroup) findViewById(R.id.dialog));

        cb_toggleLabels = (CheckBox) dialogLayout_SHOW.findViewById(R.id.cb_toggleLabels);
        cb_toggleFilled = (CheckBox) dialogLayout_SHOW.findViewById(R.id.cb_toggleFilled);

        cb_selectYX = (CheckBox) dialogLayout_SHOW.findViewById(R.id.cb_selectYX);
        rg_selectYX = (RadioGroup) dialogLayout_SHOW.findViewById(R.id.rg_selectYX);
        rb_YX = (RadioButton) dialogLayout_SHOW.findViewById(R.id.rb_YX);
        rb_Y = (RadioButton) dialogLayout_SHOW.findViewById(R.id.rb_Y);
        rb_X = (RadioButton) dialogLayout_SHOW.findViewById(R.id.rb_X);

        final ZoomType[][] zoomType = {new ZoomType[1]};
        final int[] type = {2};
        rg_selectYX.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_YX:
                        zoomType[0][0] = ZoomType.HORIZONTAL_AND_VERTICAL;
                        type[0] = 2;
                        break;
                    case R.id.rb_Y:
                        zoomType[0][0] = ZoomType.VERTICAL;
                        type[0] = 1;
                        break;
                    case R.id.rb_X:
                        zoomType[0][0] = ZoomType.HORIZONTAL;
                        type[0] = 0;
                        break;
                    default:
                        break;
                }
            }
        });


        switch (SharePrefUtil.getInt(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_TYPE,2))
        {
            case 0: rb_X.setChecked(true); break;
            case 1: rb_Y.setChecked(true); break;
            case 2: rb_YX.setChecked(true); break;
        }

        cb_toggleLabels.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_YEAR_ISCHECK,false));
        cb_toggleFilled.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_YEAR_ISCHECK,false));
        cb_selectYX.setChecked(SharePrefUtil.getBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_ISCHECK,false));

        //  初始化"对话框"——选择样式
        dialog_SHOW = new AlertDialog.Builder(this).setTitle("选择样式").setView(dialogLayout_SHOW)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLELABELS_YEAR_ISCHECK,cb_toggleLabels.isChecked());
                        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.TOGGLEFILLED_YEAR_ISCHECK,cb_toggleFilled.isChecked());
                        SharePrefUtil.saveBoolean(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_ISCHECK,cb_selectYX.isChecked());

                        placeholderFragment.toggleLabels(cb_toggleLabels.isChecked());     // 在"节点(峰点)"中显示"文本(峰点值)"
                        placeholderFragment.toggleFilled(cb_toggleFilled.isChecked());     // 区域的填充颜色(图形和坐标轴包围的区域)
                        placeholderFragment.generateData();

                        placeholderFragment.chart.setZoomEnabled(cb_selectYX.isChecked()); // 是否允许，触摸放大
                        placeholderFragment.chart.setZoomType(zoomType[0][0]);
                        SharePrefUtil.saveInt(getApplicationContext(),SharePrefUtil.REPORT_KEY.SELECTYX_YEAR_TYPE, type[0]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }




    /**
     * 更新数据
     */
    public void upViewData(String year[]) {

        tv_top_year.setText("年");

        SpecialCalendar sc = new SpecialCalendar();
        TableEx tableEx = new TableEx(getApplicationContext());
        float[][] NumbersTab = new float[1][12];
        int maxNumberOfLines = 1;
        int[] numberOfPoints = new int[]{12};

        Log.i("year[].length:",year.length+"");
        Log.i("year[0]:",year[0]+"");

        maxNumberOfLines = year.length;
        numberOfPoints = new int[maxNumberOfLines];
        NumbersTab = new float[maxNumberOfLines][12];

        for (int i = 0; i < maxNumberOfLines; i++) {
            numberOfPoints[i] = 12;
            for (int j = 0; j < 12; j++) {
                Cursor cursor = tableEx.Query(Constant.TABLE_CONSUMPTION,
                        new String[]{"sum(price)"}, "date like ? and user=?",
                        new String[]{year[i] + "-" + ToFormatUtil.toNumberFormat(j+1,2) + "-%", AccountBookApplication.getUserInfo().getUserName()},
                        null, null, null);

                if (cursor.getCount() == 0) {
                    NumbersTab[i][j] = 0;
                } else {
                    cursor.moveToNext();
                    NumbersTab[i][j] = cursor.getFloat(0);
                }
                Log.i("多年NumbersTab[" + i + "][" + j + "]:", NumbersTab[i][j] + "");
                cursor.close();
            }
        }

        placeholderFragment.setMaxNumberOfLines(maxNumberOfLines);
        placeholderFragment.setNumberOfLines(maxNumberOfLines);
        placeholderFragment.setNumberOfPoints(numberOfPoints);
        placeholderFragment.setRandomNumbersTab(NumbersTab);

        placeholderFragment.generateData();
        placeholderFragment.resetViewport();

        tableEx.closeDBConnect();
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_year:
                initAlertDialog_YearAndMonth();
                dialog_Y.create().show();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_menu:
                initAlertDialog_SHOW();
                dialog_SHOW.create().show();
                break;
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == wv_5years_start) {

            String[] yearAdapter = new String[4];
            int currentYear = Integer.valueOf(Constant.dialogYear[wv_5years_start.getCurrentItem()]); // string ==> int
            Log.i("start-end:",wv_5years_start.getCurrentItem()+"-"+ (Constant.dialogYear.length-5));
            if(wv_5years_start.getCurrentItem() < Constant.dialogYear.length-5 ){  // 45 ~ 49-5
                for(int i=0; i<4; i++){
                    yearAdapter[i] = (currentYear+i+1)+"";
                }
            }else{
                for(int i=0; i<4; i++){
                    yearAdapter[i] = (currentYear+i+1)+"";
                }
            }

            ArrayWheelAdapter<String> yearAdapter_end = new ArrayWheelAdapter<String>(this, yearAdapter);
            yearAdapter_end.setTextSize(15);                // 文本(字体)大小
            wv_5years_end.setViewAdapter(yearAdapter_end);
        }
    }


    /**
     * A fragment containing a line chart.
     * 填充成"fragment"的类
     */
    public static class PlaceholderFragment extends Fragment {

        private LineChartView chart;
        private LineChartData data;
        /** 添加了第几条线(用于在数组中找到画哪一条线)*/
        private int numberOfLines = 1;

        /** 最多4条线*/
        private int maxNumberOfLines = 1;

        /** 每一条线12个点*/
        private int[] numberOfPoints = new int[]{12};

        /** 这个应该是放置随机数的!*/
        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints[0]];

        //Axes ['æksiːz]  轴线;轴心;坐标轴
        //Shape  [ʃeɪp] 形状
        //Point [pɒɪnt] 点
        //Cubic ['kjuːbɪk] 立方体;立方的
        //Label ['leɪb(ə)l] 标签

        /** 显示/隐藏"坐标轴" true:显示坐标轴/false:隐藏坐标轴*/
        private boolean hasAxes = true;
        /** 显示/隐藏"坐标轴名称" true:显示/false:隐藏*/
        private boolean hasAxesNames = false;
        /** true:连线  false：不连线(只有点)*/
        private boolean hasLines = true;
        /** true:显示"节点(峰)"  false:去掉"节点(峰)"--就不可点击了*/
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        /** 区域的填充颜色(图形和坐标轴包围的区域) false:不填充/true:填充*/
        private boolean isFilled = false;
        /** 在"节点(峰点)"中显示"文本(峰点值)" false:不显示/true:显示*/
        private boolean hasLabels = false;
        /** false:折线/true:滑线*/
        private boolean isCubic = false;
        /** 点击"节点(峰点)"时，是否有"节点标签" true:点击时显示"峰点标签值"/ false:点击时不显示"峰点标签值"*/
        private boolean hasLabelForSelected = false;
        /** 让"节点(峰点)"变色--变一种*/
        private boolean pointsHaveDifferentColor;

        private boolean isZoom = false;     // 是否可以触摸放大
        private ZoomType zoomType = ZoomType.HORIZONTAL_AND_VERTICAL;

        public PlaceholderFragment() {
        }

        /**
         *
         * @param showLable     在"节点(峰点)"中显示"文本(峰点值)" false:不显示/true:显示
         * @param isFill        区域的填充颜色(图形和坐标轴包围的区域) false:不填充/true:填充
         * @param isZoom        是否可以触摸放大
         * @param zoomType      触摸放大类型
         */
        public PlaceholderFragment(boolean showLable, boolean isFill, boolean isZoom, int zoomType) {
            hasLabels = showLable;
            isFilled = isFill;
            this.isZoom = isZoom;

            if(zoomType == 0)this.zoomType = ZoomType.HORIZONTAL;
            else if(zoomType == 1)this.zoomType = ZoomType.VERTICAL;
            else if(zoomType == 2)this.zoomType = ZoomType.HORIZONTAL_AND_VERTICAL;

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
            View rootView = inflater.inflate(R.layout.fragment_line_chart_year, container, false);

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
            resetViewport();

            // 只有设置坐标轴拖动(不用重新设置数据)
            chart.setZoomEnabled(isZoom); // 是否允许，触摸放大
            chart.setZoomType(zoomType);

            return rootView;
        }

        /**
         * 生成一些值(用于画点的;y轴坐标;x轴默认递增) <br>
         * 存放到 ： randomNumbersTab[i][j] <br>
         * 4 条 折线              maxNumberOfLines <br>
         * 每条折线是12个点       numberOfPoints <br>
         */
        private void generateValues() {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(date);//当期日期
            String c_year = currentDate.split("-")[0];

            TableEx tableEx = new TableEx(getActivity());

            maxNumberOfLines = 1;
            numberOfLines = 1;
            numberOfPoints[0] = 12;

            for (int i = 0; i < 12; i++) {
                Cursor cursor = tableEx.Query(Constant.TABLE_CONSUMPTION,
                        new String[]{"sum(price)"}, "date like ? and user=?",
                        new String[]{c_year + "-" + ToFormatUtil.toNumberFormat(i+1,2) + "-%", AccountBookApplication.getUserInfo().getUserName()},
                        null, null, null);

                if (cursor.getCount() == 0)
                    randomNumbersTab[0][i] = 0;
                else {
                    cursor.moveToNext();
                    randomNumbersTab[0][i] = cursor.getFloat(0);
                }
                Log.i(" NumbersTab[0][" + i + "]:", randomNumbersTab[0][i] + "");
                cursor.close();
            }
            tableEx.closeDBConnect();
        }

        /***
         * 根据值，画点，处理生成"图表"--全局变量：chart
         */
        public void generateData() {
            Log.i("shen","shen1");
            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < numberOfLines; ++i) {
                Log.i("shen","shen2:"+i);
                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < numberOfPoints[i]; ++j) {
                    values.add(new PointValue(j, randomNumbersTab[i][j]));
                    Log.i("Data:NumbersTab[0][" + i + "]:", randomNumbersTab[i][j] + "");
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
            Log.i("shen","shen3");
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
        public void resetViewport() {
            // Reset viewport height range to (0,100)
            // 重置窗口高度范围(0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = 200000;
            v.left = 1;
//            v.right = numberOfPoints - 1;
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
