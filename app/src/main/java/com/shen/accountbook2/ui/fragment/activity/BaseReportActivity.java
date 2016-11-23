package com.shen.accountbook2.ui.fragment.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.shen.accountbook2.R;
import com.shen.accountbook2.config.Constant;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shen on 10/14 0014.
 */
public class BaseReportActivity extends FragmentActivity implements View.OnClickListener{

    /** 导航图片按钮*/
    public ImageButton mMeun;
    /** 返回图片按钮*/
    public ImageButton mBack;
    /** 当前Activity的文本*/
    public TextView mTitle;

    /** 从系统中拿到当前日期*/
    public String currentDate = "";       // 从系统中拿到当前日期
    public int year_c = 0;
    public int month_c = 0;
    public int day_c = 0;

    /** 拿到当前"系统年份"在Constant.dialogYear数组中的"索引"*/
    public int index_year = 0;
    /** 拿到当前"系统年份"在Constant.dialogMonth数组中的"索引"*/
    public int index_month = 0;

    public BaseReportActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initListener();
        initData();
    }

    public void initView(){
        mBack = (ImageButton) findViewById(R.id.btn_back);
        mMeun = (ImageButton) findViewById(R.id.btn_menu);
        mTitle = (TextView) findViewById(R.id.tv_title);
    }

    public void initListener(){
        mBack.setOnClickListener(this);
        mMeun.setOnClickListener(this);
    }

    public void initData(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);  //当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);

        // 拿到当前"系统年份"在Constant.dialogYear数组中的"索引"
        for (index_year = 0; index_year < Constant.dialogYear.length; index_year++) {
            if (String.valueOf(year_c).equals(Constant.dialogYear[index_year]))
                break;
        }
        // 拿到当前"系统年份"在Constant.dialogMonth数组中的"索引"
        for (index_month = 0; index_month < Constant.dialogMonth.length; index_month++) {
            if (String.valueOf(month_c).equals(Constant.dialogMonth[index_month]))
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
