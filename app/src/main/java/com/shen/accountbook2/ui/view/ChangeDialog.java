package com.shen.accountbook2.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shen.accountbook2.R;

/**
 * Created by shen on 10/30 0030.
 */
public abstract class ChangeDialog extends AlertDialog implements View.OnClickListener{

    private TextView tvTitle;
    private EditText etChange;

    private Button btnConfirm;
    private Button btnCancel;

    protected ChangeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change);

        initView();
        initListener();
        initData();
    }

    private void initView(){

        tvTitle = (TextView) findViewById(R.id.tv_title);
        etChange = (EditText) findViewById(R.id.et_change);

        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnCancel = (Button) findViewById(R.id.btn_cancel);


    }

    private void initListener(){
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void initData(){
        tvTitle.setText("更改QQ");

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
        System.out.println("params.flags:"+Integer.toHexString(params.flags));
        window.setAttributes(params);

    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    /**
     * 点击"确定按钮"干的事情
     * @return View
     */
    public abstract void confirm(String text);
    /**
     * 点击"取消按钮"干的事情
     * @return View
     */
    public abstract void cancel();


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_confirm:
                confirm(etChange.getText().toString());
                break;
            case R.id.btn_cancel:
                cancel();
                break;
            default:
                break;
        }
    }
}