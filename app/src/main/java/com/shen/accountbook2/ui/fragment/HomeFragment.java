package com.shen.accountbook2.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.LogUtils;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.ui.AddActivity;
import com.shen.accountbook2.ui.fragment.activity.ReportForD_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForM_LineChar_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForMixture_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForType_LineChar_Activity;
import com.shen.accountbook2.ui.fragment.activity.ReportForY_LineChar_Activity;

/**
 * Created by shen on 9/9 0009.
 */
public class HomeFragment extends BaseFragment{

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    /******************************标题***********************************/
    private TextView tvTitle;


    private Button mBtnReportMonth;         // 月报表
    private Button mBtnReportYear;          // 年报表
    private Button mBtnReportType;          // 类型报表
    private Button mBtnReportMixture;       // 混合报表
    private Button mBtnReportDay;           // 日报表

    private Button mBtnAdd;                 // 添加按钮

    public HomeFragment() {
    }

    @Override
    public View initUI() {

        // 將 R.layout.base_pager布局 填充成 view,作为其布局
        View view = View.inflate(mContext, R.layout.fragment_home, null);

        /******************************标题***********************************/
        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        mBtnReportMonth = (Button) view.findViewById(R.id.btn_M);
        mBtnReportYear = (Button) view.findViewById(R.id.btn_Y);
        mBtnReportType = (Button) view.findViewById(R.id.btn_type);

        mBtnReportMixture = (Button) view.findViewById(R.id.btn_Mixture);
        mBtnReportDay = (Button) view.findViewById(R.id.btn_D);

        mBtnAdd = (Button) view.findViewById(R.id.btn_add);

        return view;
    }

    @Override
    public void initListener() {
        mBtnReportMonth.setOnClickListener(this);
        mBtnReportYear.setOnClickListener(this);
        mBtnReportType.setOnClickListener(this);

        mBtnReportMixture.setOnClickListener(this);
        mBtnReportDay.setOnClickListener(this);

        mBtnAdd.setOnClickListener(this);
    }


    @Override
    public void initData(){

        tvTitle.setText("首页");
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        LogUtils.i("HomeFragment:========isPrepared:"+isPrepared+"=======isVisible:"+isVisible);
        if(!isPrepared || !isVisible) {
            return;
        }
        //填充各控件的数据
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

                case R.id.btn_M:                       // 月报表
                    intent = new Intent(mContext, ReportForM_LineChar_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_Y:                       // 年报表
                    intent = new Intent(mContext, ReportForY_LineChar_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_type:                    // 类型报表
                    intent = new Intent(mContext, ReportForType_LineChar_Activity.class);
                    startActivity(intent);
                    break;


                case R.id.btn_Mixture:                     // 混合类报表
                    intent = new Intent(mContext, ReportForMixture_Activity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_add:                      // 添加"消费"
                    intent = new Intent(mContext, AddActivity.class);
                    // 解决在非Activity中使用startActivity;添加这个,如果要使用这种方式需要打开新的TASK。
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;

                default:break;
            }
        }else
            ToastUtil.show("请登陆后再操作!");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.i("来了这里了：HomeFragment");
    }

}
