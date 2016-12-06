package com.shen.accountbook2.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.CreateFilesUtils;
import com.shen.accountbook2.Utils.LogUtils;
import com.shen.accountbook2.Utils.SharePrefUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.domain.UserInfo;
import com.shen.accountbook2.global.AccountBookApplication;

import java.io.File;


/**
 * Created by shen on 9/1 0001.
 */
public class LoginActivity extends Activity {

    public static final int OK = 1;

    private ImageButton mMeun;
    private ImageButton mBack;
    private TextView mTitle;

    private EditText mUsename;
    private EditText mPassword;

    /**记录登录密码*/
    private CheckBox mRemember;
    private CheckBox mAotu;

    private Button mLogin;

    private String c_name;
    private String c_password;
    private int c_sex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListend();
        initData();

    }

    private void initView() {
        mBack = (ImageButton) findViewById(R.id.btn_back);
        mMeun = (ImageButton) findViewById(R.id.btn_menu);
        mTitle = (TextView) findViewById(R.id.tv_title);

        mUsename = (EditText) findViewById(R.id.login_et_username);
        mPassword = (EditText) findViewById(R.id.login_et_password);

        mRemember = (CheckBox) findViewById(R.id.login_cb_remember);
        mAotu = (CheckBox) findViewById(R.id.login_cb_auto);
        mLogin = (Button) findViewById(R.id.login_btn_login);
    }

    private void initListend() {
        mMeun.setVisibility(View.GONE);
        mBack.setVisibility(View.VISIBLE);
        mTitle.setText("登录界面");

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUsename.getText().toString()) ||
                        TextUtils.isEmpty(mPassword.getText().toString())) {
                    Toast.makeText(getBaseContext(), "用户和密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    TableEx tableEx = new TableEx(AccountBookApplication.getContext());

                    Cursor cursor = tableEx.Query(Constant.TABLE_USER,new String[]{"name,password,sex,image,birthday,qq"}, "name=? and password=?",
                            new String[]{mUsename.getText().toString(),mPassword.getText().toString()},null,null,null);

                    if (!cursor.moveToFirst() == false) {
                        c_name = cursor.getString(0);
                        c_password = cursor.getString(1);
                        c_sex = cursor.getInt(2);

                        String c_image = cursor.getString(3);
                        String c_birthday = cursor.getString(4);
                        String c_qq = cursor.getString(5);

                        // 登录标志，登录信息填到"全局变量"中
                        AccountBookApplication.setIsLogin(true);
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUserName(c_name);
                        userInfo.setPassWord(c_password);
                        userInfo.setSex(c_sex);

                        userInfo.setImage(c_image);
                        userInfo.setBirthday(c_birthday);
                        userInfo.setQq(c_qq);

                        AccountBookApplication.setUserInfo(userInfo);

                        Toast.makeText(getBaseContext(), c_name + ":" + c_password + ":" + c_sex, Toast.LENGTH_SHORT).show();

                        if (mRemember.isChecked()) {
                            SharePrefUtil.saveBoolean(getBaseContext(), SharePrefUtil.KEY.REMEMBER_ISCHECK, mRemember.isChecked());
                            SharePrefUtil.saveString(getBaseContext(), SharePrefUtil.KEY.USENAME, mUsename.getText().toString());
                            SharePrefUtil.saveString(getBaseContext(), SharePrefUtil.KEY.PASSWORK, mPassword.getText().toString());
                        } else {
                            SharePrefUtil.saveBoolean(getBaseContext(), SharePrefUtil.KEY.REMEMBER_ISCHECK, mRemember.isChecked());
                            SharePrefUtil.saveString(getBaseContext(), SharePrefUtil.KEY.USENAME, "");
                            SharePrefUtil.saveString(getBaseContext(), SharePrefUtil.KEY.PASSWORK, "");
                        }

                        if (mAotu.isChecked())
                            SharePrefUtil.saveBoolean(getBaseContext(), SharePrefUtil.KEY.AUTO_ISCHECK, mAotu.isChecked());
                        else
                            SharePrefUtil.saveBoolean(getBaseContext(), SharePrefUtil.KEY.AUTO_ISCHECK, mAotu.isChecked());


                        File files = CreateFilesUtils.create(Constant.IMAGE_PATH + c_name);     // 创建"用户文件夹"
                        if(files.exists())
                            LogUtils.i("Login当前用户文件夹"+files.getAbsolutePath());

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();

                        bundle.putBoolean("isLogin",true);
                        intent.putExtras(bundle);
                        setResult(OK,intent);

                        finish();
                    }
                    else{
                        Toast.makeText(getBaseContext(),"用户或密码错误", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                }
            }
        });
    }

    /**
     * 如果"记住登记密码"就将之前的填写
     */
    private void initData() {
        Boolean auto = SharePrefUtil.getBoolean(this, SharePrefUtil.KEY.AUTO_ISCHECK, false);
        Boolean remember = SharePrefUtil.getBoolean(this, SharePrefUtil.KEY.REMEMBER_ISCHECK, false);

        mAotu.setChecked(auto);
        mRemember.setChecked(remember);

        if(remember){
            String mSp_user = SharePrefUtil.getString(this, SharePrefUtil.KEY.USENAME, "");
            String mSp_password = SharePrefUtil.getString(this, SharePrefUtil.KEY.PASSWORK, "");

            mUsename.setText(mSp_user);
            mPassword.setText(mSp_password);
        }
    }

}
