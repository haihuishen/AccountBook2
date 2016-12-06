package com.shen.accountbook2.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.LogUtils;

/**
 * Created by shen on 10/30 0030.
 */
public abstract class VersionUpdateDialog extends AlertDialog implements View.OnClickListener{

    private TextView mTvTitle;

    private TextView mTvCurrentVersion;         // 旧版本
    private TextView mTvNewVersion;             // 新版本
    private TextView mTvVersionDescribe;         // 版本描述

    private String mTitle;                   // 标题
    private String mCurrentVersionName="";         // 旧版本
    private String mNewVersionName="";             // 新版本
    private String mVersionDescribe="";         // 版本描述

    private Button mBtnUp;                      // 立即更新
    private Button mBtnCancel;                  // 稍后再说

    protected VersionUpdateDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_version_update);
        // 按空白处不能取消动画
        // 安卓弹出"窗口"之后触摸屏幕就消失了的解决方法
        setCanceledOnTouchOutside(false);

        initView();
        initListener();
        initData();
    }

    private void initView(){

        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mTvCurrentVersion = (TextView) findViewById(R.id.tv_current_version);
        mTvNewVersion = (TextView) findViewById(R.id.tv_new_version);
        mTvVersionDescribe = (TextView) findViewById(R.id.tv_describe);

        mBtnUp = (Button) findViewById(R.id.btn_up);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);


    }

    private void initListener(){
        mBtnUp.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        /***点击返回的事件***/
        //点击取消事件监听（没有点击"更新"或"稍后更新"而是直接点击"返回按钮"）
        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消
                dialog.dismiss();	// 关闭对话框
            }
        });
    }

    private void initData(){
        mTvTitle.setText("版本更新");

        // 设置此窗口的设置
        Window window = this.getWindow();
        // window.setContentView(R.layout.dialog_change);   // 最好不要弄这个，反正是不成功的
        WindowManager.LayoutParams params = window.getAttributes();
        // params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        // params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        // params.dimAmount=0.5f;//设置对话框的透明程度背景(非布局的透明度)

        // params.flags = 0x01810100; 这个是能使用"edittext"和"button"
        // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN  ==>0x00000100 ==> 设置了这个;弹出输入法时，不会将"对话框"顶上去
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH |
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;

        LogUtils.i("params.flags:"+Integer.toHexString(params.flags));

        //params.alpha = (float) 0.5;  //透明度(背景)     （0.0-1.0）
        window.setAttributes(params);

        mTvCurrentVersion.setText(mCurrentVersionName);
        mTvNewVersion.setText(mNewVersionName);
        mTvVersionDescribe.setText(mVersionDescribe);
    }

    /**
     * 设置"标题"
     * @param title 标题
     */
    public void setTitle(String title){
        if(mTvTitle != null)                // 在子类中，new 了窗口，马上设置这个,会是"空指针";因为 .show() 之后才 onCreate
            mTvTitle.setText(title);
        else
            mTitle = title;
    }

    /**
     * 设置"当前版本名称"
     * @param currentVersionName
     */
    public void setCurrentVersionName(String currentVersionName){
        if(mTvCurrentVersion != null)// 在子类中，new 了窗口，马上设置这个,会是"空指针";因为 .show() 之后才 onCreate
            mTvCurrentVersion.setText(currentVersionName);
        else
            mCurrentVersionName = currentVersionName;
    }

    /**
     * 设置"最新版本名称"
     * @param newVersionName
     */
    public void setNewVersionName(String newVersionName){
        if(mTvNewVersion != null)// 在子类中，new 了窗口，马上设置这个,会是"空指针";因为 .show() 之后才 onCreate
            mTvNewVersion.setText(newVersionName);
        else
            mNewVersionName = newVersionName;
    }

    /**
     * 设置"最新版本描述"
     * @param versionDescribe
     */
    public void setVersionDescribe(String versionDescribe){
        if(mTvVersionDescribe != null)// 在子类中，new 了窗口，马上设置这个,会是"空指针";因为 .show() 之后才 onCreate
            mTvVersionDescribe.setText(versionDescribe);
        else
            mVersionDescribe = versionDescribe;
    }

    /**
     * 点击"确定按钮"干的事情
     * @return View
     */
    public abstract void up();
    /**
     * 点击"取消按钮"干的事情
     * @return View
     */
    public abstract void cancel();



    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_up:
                up();
                break;
            case R.id.btn_cancel:
                cancel();
                break;
            default:
                break;
        }
    }
}
