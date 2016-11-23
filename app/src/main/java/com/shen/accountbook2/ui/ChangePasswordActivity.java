package com.shen.accountbook2.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;

/**
 * Created by shen on 11/3 0003.
 */
public class ChangePasswordActivity extends Activity implements View.OnClickListener{

    private TextView tvTitle;
    private ImageButton btnMenu;
    private ImageButton btnBack;

    private EditText etOldPassWord;
    private EditText etNewPassWord;
    private EditText etNewPassWordConfirm;

    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initView();
        initListener();
        initData();
    }

    private void initView(){
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        etOldPassWord = (EditText) findViewById(R.id.et_old_password);
        etNewPassWord = (EditText) findViewById(R.id.et_new_password);
        etNewPassWordConfirm = (EditText) findViewById(R.id.et_new_password_confirm);

        btnConfirm = (Button) findViewById(R.id.btn_confirm);

    }

    private void initListener(){
        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    private void initData(){
        tvTitle.setText("修改密码");
        btnMenu.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);
    }

    /**
     * 修改密码
     */
    private void changePassword(){
        if(!TextUtils.isEmpty(etOldPassWord.getText().toString())){
            if(AccountBookApplication.getUserInfo().getPassWord().equals(etOldPassWord.getText().toString())){
                if(!TextUtils.isEmpty(etNewPassWord.getText())){
                    if(etNewPassWord.getText().toString().length() >= 6){
                        if(!TextUtils.isEmpty(etNewPassWordConfirm.getText())){
                            if(etNewPassWord.getText().toString().equals(etNewPassWordConfirm.getText().toString())){

                                TableEx tableEx = new TableEx(this.getApplication());

                                ContentValues values = new ContentValues();
                                values.put("password", etNewPassWordConfirm.getText().toString());
                                int num = tableEx.Update(Constant.TABLE_USER, values, "password=?", new String[]{etOldPassWord.getText().toString()});

                                if(num != 0) {
                                    AccountBookApplication.getUserInfo().setPassWord(etNewPassWordConfirm.getText().toString());
                                    ToastUtil.show("修改成功");
                                }else{
                                    ToastUtil.show("修改失败");
                                }
                            }else{
                                ToastUtil.show("两次输入的新密码不同");
                            }
                        }else {
                            ToastUtil.show("请再次输入新密码");
                        }
                    }else{
                        ToastUtil.show("输入的密码不能小于6位");
                    }
                }else {
                    ToastUtil.show("请输入新密码");
                }
            }else
                ToastUtil.show("您输入的旧密码有误");
        }else{
            ToastUtil.show("请输入旧密码");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:                                         // 退出本Activity
                finish();
                break;

            case R.id.btn_confirm:                               // 确认
                changePassword();
                ToastUtil.show("确认");
                break;
        }
    }
}
