package com.shen.accountbook2.ui.fragment.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shen.accountbook2.R;

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

    }

    @Override
    public void onClick(View v) {

    }
}
