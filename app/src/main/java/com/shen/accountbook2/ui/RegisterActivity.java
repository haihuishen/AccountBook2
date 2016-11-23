package com.shen.accountbook2.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shen.accountbook2.R;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;


/**
 * Created by shen on 9/1 0001.
 */
public class RegisterActivity extends Activity{


    private ImageButton mMeun;
    private ImageButton mBack;
    private TextView mTitle;

    private EditText mUsename;
    private EditText mPassword;
    private RadioGroup mSex;
    private int Sex;

    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initListend();
    }


    private void initView(){
        mBack = (ImageButton) findViewById(R.id.btn_back);
        mMeun = (ImageButton) findViewById(R.id.btn_menu);
        mTitle = (TextView) findViewById(R.id.tv_title);

        mUsename = (EditText) findViewById(R.id.register_et_username);
        mPassword = (EditText) findViewById(R.id.register_et_password);
        mSex = (RadioGroup) findViewById(R.id.register_rg_sex);

        mRegister = (Button) findViewById(R.id.register_btn_register);
    }

    private void initListend(){
        mMeun.setVisibility(View.GONE);
        mBack.setVisibility(View.VISIBLE);
        mTitle.setText("注册界面");

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.register_rb_man:
                        Sex = 1;
                        System.out.println(Sex);
                        break;
                    case R.id.register_rb_woman:
                        Sex = 0;
                        System.out.println(Sex);
                        break;
                }
            }
        });

        // 拿到选中的 RadioButton
        if(mSex.getCheckedRadioButtonId() == R.id.register_rb_man)
            Sex = 1;
        else
            Sex = 0;

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(mUsename.getText().toString()) ||
                        TextUtils.isEmpty(mPassword.getText().toString())) {
                    Toast.makeText(getBaseContext(), "用户和密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    TableEx tableEx = new TableEx(AccountBookApplication.getContext());
                    Cursor cursor = tableEx.Query(Constant.TABLE_USER, new String[]{"name"}, "name=?",
                                new String[]{mUsename.getText().toString()},null,null,null);
                    String c_name = "";
                    if(cursor.getCount() >= 1) {
                        cursor.moveToNext();
                        c_name = cursor.getString(0);
                    }

                    if(!(c_name.equals(mUsename.getText().toString()))){
                        try {
                            ContentValues values = new ContentValues();
                            values.put("name", mUsename.getText().toString());                        // 字段  ： 值
                            values.put("password", mPassword.getText().toString());
                            values.put("sex", Sex);
                            tableEx.Add(Constant.TABLE_USER, values);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finish();
                    }else{
                        Toast.makeText(getBaseContext(), "此用户已存在!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
