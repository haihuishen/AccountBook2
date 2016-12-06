package com.shen.accountbook2.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.ImageFactory;
import com.shen.accountbook2.Utils.LogUtils;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.domain.UserInfo;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.ui.AddActivity;
import com.shen.accountbook2.ui.AssetManagerActivity;
import com.shen.accountbook2.ui.LoginActivity;
import com.shen.accountbook2.ui.RegisterActivity;
import com.shen.accountbook2.ui.fragment.activity.MineInfoActivity;
import com.shen.accountbook2.ui.view.CircleImageView;

import java.io.File;

/**
 * Created by shen on 9/9 0009.
 * 我的
 */
public class MineFragment extends BaseFragment{

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    private Button mBtnLogin;
    private Button mBtnRegister;

    private static final int REQUEST_LOGIN = 1;             // 登录
    private static final int REQUEST_ASSETS = 2;            // 资产管理
    private static final int REQUEST_MINE_INFO = 3;         // 用户信息
    private static final int REQUEST_ADD = 4;               // 添加消费

    private CircleImageView mCivHead;
    private Bitmap mBitmap;

    private TextView mTvUser;
    private ImageView mIvSex;
    private Button mLoginFinishLogout;

    private LinearLayout mLayoutLogin;
    private RelativeLayout mLayoutLogout;
    private LinearLayout mLayoutInfo;              // 包裹用户信息的布局


    // 总资产
    private LinearLayout mLayoutTotalAssets;        // 包裹总资产的布局
    private TextView mTvTotalAssets;                 // 总资产
    private TextView mTvChangeTime;                  // "总资产"修改时间
    private TextView mTvActualTotalAssets;           // 实际总资产

    private Cursor cursorTotalAssets;               // 总资产
    private Cursor cursorActualTotalAssets;        // 实际总资产
    private TableEx tableEx;


    private Button mBtnAdd;                 // 添加按钮

    public MineFragment() {
    }

    @Override
    public View initUI() {

        // 將 R.layout.base_pager布局 填充成 view,作为其布局
        View view = View.inflate(mContext, R.layout.fragment_mine, null);

        mBtnLogin = (Button) view.findViewById(R.id.bt_login);
        mBtnRegister = (Button) view.findViewById(R.id.bt_register);

        mLayoutLogin = (LinearLayout) view.findViewById(R.id.layout_login_finish);
        mLayoutLogout = (RelativeLayout) view.findViewById(R.id.layout_login);
        mLayoutInfo = (LinearLayout) view.findViewById(R.id.layout_info);

        mCivHead = (CircleImageView) view.findViewById(R.id.CircleImage_head);

        mTvUser = (TextView) view.findViewById(R.id.tv_user);
        mIvSex = (ImageView) view.findViewById(R.id.iv_sex);
        mLoginFinishLogout = (Button) view.findViewById(R.id.btn_logout);


        mLayoutTotalAssets = (LinearLayout) view.findViewById(R.id.layout_total_assets);
        mTvTotalAssets = (TextView) view.findViewById(R.id.tv_total_assets);
        mTvChangeTime = (TextView) view.findViewById(R.id.tv_change_time);
        mTvActualTotalAssets = (TextView) view.findViewById(R.id.tv_actual_total_assets);

        mBtnAdd = (Button) view.findViewById(R.id.btn_add);

        return view;
    }

    @Override
    public void initListener() {
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mLoginFinishLogout.setOnClickListener(this);
        mLayoutInfo.setOnClickListener(this);

        mLayoutTotalAssets.setOnClickListener(this);

        mBtnAdd.setOnClickListener(this);
    }

    @Override
    public void initData(){
        login();
        isPrepared = true;
        lazyLoad();
    }

    /**
     * 根据登录标志，登录
     */
    private void login(){
        if(AccountBookApplication.isLogin()){
            UserInfo userInfo = AccountBookApplication.getUserInfo();

            if(userInfo != null){
                mTvUser.setText(userInfo.getUserName());
                mIvSex.setImageResource(userInfo.getSex() == 1 ? R.mipmap.man : R.mipmap.woman);
                mLayoutLogout.setVisibility(View.GONE);
                mLayoutLogin.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(userInfo.getImage()) &&
                        new File(userInfo.getImage()).exists()){                // 如果"注册用户"没有图片
                    mBitmap = ImageFactory.getBitmap(userInfo.getImage());
                }else {                                                     // 如果"注册用户"没有图片
                    userInfo.setImage(Constant.CACHE_IMAGE_PATH + "cat_head.png");
                    AccountBookApplication.setUserInfo(userInfo);           //
                    mBitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "cat_head.png");
                }
                mCivHead.setImageBitmap(mBitmap);
                getTotalAssets();
            }
        }
        else{
            AccountBookApplication.setIsLogin(false);
            AccountBookApplication.setUserInfo(null);
            mLayoutLogout.setVisibility(View.VISIBLE);
            mLayoutLogin.setVisibility(View.GONE);
        }
    }

    @Override
    protected void lazyLoad() {
        LogUtils.i("MineFragment:========isPrepared:"+isPrepared+"=======isVisible:"+isVisible);
        if(!isPrepared || !isVisible) {
            return;
        }
        if(AccountBookApplication.isLogin()){
            UserInfo userInfo = AccountBookApplication.getUserInfo();
            if(userInfo != null){
                getTotalAssets();
            }
        }
    }

    /**
     * 获取"总资产"
     */
    public void getTotalAssets(){
        if(AccountBookApplication.isLogin()){
            tableEx = new TableEx(AccountBookApplication.getContext());

            String totalAssets = "0";
            // cast(sum(asset) as TEXT)--> 这样就不会变成"科学计数法"
            // sum(asset) -->asset 就算是 varchar(20),不是decimal(18,2)，使用sum(asset)后还是"会使用科学计数法"
            cursorTotalAssets = tableEx.Query(Constant.TABLE_ASSETS, new String[]{"cast(sum(asset) as TEXT),max(changetime)"},
                    "user=?", new String[]{AccountBookApplication.getUserInfo().getUserName()},
                    null, null, null);
            try {
                if (cursorTotalAssets.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                    cursorTotalAssets.moveToFirst();
                    if(cursorTotalAssets.getString(0) != null){        // 没记录，返回1，这里返回的是 "null"
                        if(Float.valueOf(cursorTotalAssets.getString(0)) >= 0) {
                            mTvTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                            mTvTotalAssets.setText("总资产: " + ToFormatUtil.stringToDecimalFormat(cursorTotalAssets.getString(0), 2));
                            mTvChangeTime.setText("总资产最后修改时间: " + cursorTotalAssets.getString(1));
                            totalAssets = ToFormatUtil.stringToDecimalFormat(cursorTotalAssets.getString(0), 2);
                        }else{
                            mTvTotalAssets.setTextColor(this.getResources().getColor(R.color.red));
                            mTvTotalAssets.setText("总资产: " + ToFormatUtil.stringToDecimalFormat(cursorTotalAssets.getString(0), 2));
                            mTvChangeTime.setText("总资产最后修改时间: " + cursorTotalAssets.getString(1));
                            totalAssets = ToFormatUtil.stringToDecimalFormat(cursorTotalAssets.getString(0), 2);
                        }
                    }else{
                        mTvTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                        mTvTotalAssets.setText("总资产: 0");
                        mTvChangeTime.setText("");
                    }
                }else{
                    mTvTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                    mTvTotalAssets.setText("总资产: 0");
                    mTvChangeTime.setText("");
                }
            }catch (Exception e){
                System.out.println("总资产error:"+e.getMessage());
            }

            cursorActualTotalAssets = tableEx.getDb().rawQuery("select cast(sum(a.asset)-(select sum(price)" +
                            "from consumption where date>(select max(changetime) from assets where user=?) and user=?) as TEXT)" +
                            "from assets as a where user=?",
                    new String[]{AccountBookApplication.getUserInfo().getUserName(),
                            AccountBookApplication.getUserInfo().getUserName(),
                            AccountBookApplication.getUserInfo().getUserName()});

            try {
                if (cursorActualTotalAssets.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                    cursorActualTotalAssets.moveToFirst();
                    if(cursorActualTotalAssets.getString(0) != null){        // 没记录，返回1，这里返回的是 "null"
                        if(Float.valueOf(cursorActualTotalAssets.getString(0)) >= 0) {
                            mTvActualTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                            mTvActualTotalAssets.setText("实际总资产: " + ToFormatUtil.stringToDecimalFormat(cursorActualTotalAssets.getString(0), 2));
                        }else{
                            mTvActualTotalAssets.setTextColor(this.getResources().getColor(R.color.red));
                            mTvActualTotalAssets.setText("实际总资产: " + ToFormatUtil.stringToDecimalFormat(cursorActualTotalAssets.getString(0), 2));
                        }
                    }else{
                        mTvActualTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                        mTvActualTotalAssets.setText("实际总资产: "+totalAssets);
                    }
                }else{
                    mTvActualTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                    mTvActualTotalAssets.setText("实际总资产: +"+totalAssets);
                }
            }catch (Exception e){
                System.out.println("实际总资产error:"+e.getMessage());
            }
        }else{
            mTvTotalAssets.setText("总资产: 0");
            mTvActualTotalAssets.setText("实际总资产: 0");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.i("来了这里了：MineFragmnet");
        if(requestCode == REQUEST_LOGIN){
            if(resultCode == LoginActivity.OK){
                Bundle bundle = data.getExtras();
                boolean b = bundle.getBoolean("isLogin");
                if(b) {
                    login();
                }
            }
        }
        if(requestCode == REQUEST_ASSETS){
            if(resultCode == AssetManagerActivity.OK){
                Bundle bundle = data.getExtras();
                boolean b = bundle.getBoolean("refresh");
                if(b) {
                    login();
                }
            }
        }
        if(requestCode == REQUEST_MINE_INFO){
            if(resultCode == MineInfoActivity.OK){
                Bundle bundle = data.getExtras();
                boolean b = bundle.getBoolean("refresh");
                if(b) {
                    login();
                }
            }
        }
        if(requestCode == REQUEST_ADD){
            if(resultCode == AddActivity.OK){
                Bundle bundle = data.getExtras();
                boolean b = bundle.getBoolean("refresh");
                if(b) {
                    login();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.bt_login:                                             // 登录
                intent = new Intent(mContext, LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
                break;
            case R.id.bt_register:                                          // 注册
                intent = new Intent(mContext, RegisterActivity.class);
                // 解决在非Activity中使用startActivity;添加这个,如果要使用这种方式需要打开新的TASK。
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_logout:                                           // 注销
                if(AccountBookApplication.isLogin()) {
                    AccountBookApplication.setIsLogin(false);
                    AccountBookApplication.setUserInfo(null);
                    mLayoutLogout.setVisibility(View.VISIBLE);
                    mLayoutLogin.setVisibility(View.GONE);

                    mTvActualTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                    mTvActualTotalAssets.setText("实际总资产: 0");
                    mTvTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                    mTvTotalAssets.setText("总资产: 0");
                    mTvChangeTime.setText("");
                }else
                    ToastUtil.show("请登陆后再操作!");
                break;
            case R.id.layout_info:                                          // 前往"更用户详细的界面"
                if (AccountBookApplication.isLogin()) {                           // 只有登录后才能进去
                    intent = new Intent(mContext, MineInfoActivity.class);
                    startActivityForResult(intent, REQUEST_MINE_INFO);
                }
                break;

            case R.id.layout_total_assets:                                  // 点击"总资产layout"跳到资产管理界面
                if(AccountBookApplication.isLogin()) {
                    intent = new Intent(mContext, AssetManagerActivity.class);
                    startActivityForResult(intent, REQUEST_ASSETS);
                }else
                    ToastUtil.show("请登陆后再操作!");
                break;

            case R.id.btn_add:                      // 添加"消费"
                if(AccountBookApplication.isLogin()) {
                    intent = new Intent(mContext, AddActivity.class);
                    startActivityForResult(intent, REQUEST_ADD);
                }else
                    ToastUtil.show("请登陆后再操作!");
                break;

            default:
                break;

        }


    }
}
