package com.shen.accountbook2.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.TimeCompare;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shen on 11/6 0006.
 */
public class ChangeAssetActivity extends Activity implements View.OnClickListener{

    public static Context mContext;

    public static final int OK = 1;
    public static final String CHANGE_ASSET= "changeAsset";

    private String assetType;           // 从AssetManagerActivity 传过来的"资产类型"
    private int id;                     // 从AssetManagerActivity 传过来的"在asset表的 _id"
    private String changeTime;
    private String what;
    private String bankNum;
    private String asset;

    private TableEx tableEx;
    private Cursor cursor;

    /******************************标题***********************************/
    private TextView tvTitle;
    private ImageButton btnMenu;
    private ImageButton btnBack;
    private Button btnFinish;

    /******************************选择资产类型***********************************/
    TextView tvAssetType;                                   // 选择资产类型

    /** 时间选择器*/
    TimePickerView pvTime;

    /******************************信用卡布局***********************************/
    LinearLayout layoutCreditCard;                            // 信用卡布局
    TextView tvCreditChangeTime;                             // 修改金额时间
    EditText etCreditWhat;                                    // 什么银行的信用卡
    EditText etCreditCardNum;                                 // 银行卡后面4位
    TextView tvCreditAsset;                                   // 资源
    EditText etCreditAsset;                                   // 资源

    /******************************储蓄卡布局***********************************/
    LinearLayout layoutDepositCard;                                // 储蓄卡布局
    TextView tvDepositChangeTime;                             // 修改金额时间
    EditText etDepositWhat;                                    // 什么银行的储蓄卡
    EditText etDepositCardNum;                                 // 银行卡后面4位
    TextView tvDepositAsset;                                   // 资源
    EditText etDepositAsset;                                   // 资源

    /******************************借贷公司***********************************/
    LinearLayout layoutCompany;                                // 借贷公司布局
    TextView tvCompanyChangeTime;                             // 修改金额时间
    EditText etCompanyWhat;                                    // 什么借贷公司
    TextView tvCompanyAsset;                                   // 资源
    EditText etCompanyAsset;                                   // 资源

    /******************************第三方支付***********************************/
    LinearLayout layoutEcpss;                                // 第三方支付布局
    TextView tvEcpssChangeTime;                             // 修改金额时间
    EditText etEcpssWhat;                                    // 哪家滴三方支付
    TextView tvEcpssAsset;                                   // 资源
    EditText etEcpssAsset;                                   // 资源

    /******************************欠别人钱***********************************/
    LinearLayout layoutOweOther;                                // 欠别人钱布局
    TextView tvOweOtherChangeTime;                             // 修改金额时间
    EditText etOweOtherWhat;                                    // 欠谁的钱
    TextView tvOweOtherAsset;                                   // 资源
    EditText etOweOtherAsset;                                   // 资源

    /******************************我是债主***********************************/
    LinearLayout layoutOweMe;                                // 我是债主布局
    TextView tvOweMeChangeTime;                             // 修改金额时间
    EditText etOweMeWhat;                                     // 谁欠我的钱
    TextView tvOweMeAsset;                                   // 资源
    EditText etOweMeAsset;                                   // 资源

    /******************************个人现金***********************************/
    LinearLayout layoutMe;                                // 个人现金布局
    TextView tvMeChangeTime;                             // 修改金额时间
    TextView tvMeAsset;                                   // 资源
    EditText etMeAsset;                                   // 资源

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);

        mContext = this;

        Intent intent = getIntent();
        //获取数据
        Bundle data = intent.getExtras();
        assetType = data.getString("assetType");
        id = data.getInt("_id");

        initView();
        initListener();
        initData();
    }

    private void initView(){
        /******************************标题***********************************/
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnFinish = (Button) findViewById(R.id.btn_title_confirm);

        /******************************选择资产类型***********************************/
        tvAssetType = (TextView) findViewById(R.id.tv_asset_type);

        /******************************信用卡布局***********************************/
        layoutCreditCard = (LinearLayout) findViewById(R.id.layout_credit_card);
        tvCreditChangeTime = (TextView) findViewById(R.id.tv_credit_change_time);
        etCreditWhat = (EditText) findViewById(R.id.et_credit_what);
        etCreditCardNum = (EditText) findViewById(R.id.et_credit_card_num);
        tvCreditAsset = (TextView) findViewById(R.id.tv_credit_asset);
        etCreditAsset = (EditText) findViewById(R.id.et_credit_asset);

        /******************************储蓄卡布局***********************************/
        layoutDepositCard = (LinearLayout) findViewById(R.id.layout_deposit_card);
        tvDepositChangeTime = (TextView) findViewById(R.id.tv_deposit_change_time);
        etDepositWhat = (EditText) findViewById(R.id.et_deposit_what);
        etDepositCardNum = (EditText) findViewById(R.id.et_deposit_card_num);
        tvDepositAsset = (TextView) findViewById(R.id.tv_deposit_asset);
        etDepositAsset = (EditText) findViewById(R.id.et_deposit_asset);

        /******************************借贷公司布局***********************************/
        layoutCompany = (LinearLayout) findViewById(R.id.layout_company);
        tvCompanyChangeTime = (TextView) findViewById(R.id.tv_company_change_time);
        etCompanyWhat = (EditText) findViewById(R.id.et_company_what);
        tvCompanyAsset = (TextView) findViewById(R.id.tv_company_asset);
        etCompanyAsset = (EditText) findViewById(R.id.et_company_asset);

        /******************************第三方支付***********************************/
        layoutEcpss = (LinearLayout) findViewById(R.id.layout_ecpss);
        tvEcpssChangeTime = (TextView) findViewById(R.id.tv_ecpss_change_time);
        etEcpssWhat = (EditText) findViewById(R.id.et_ecpss_what);
        tvEcpssAsset = (TextView) findViewById(R.id.tv_ecpss_asset);
        etEcpssAsset = (EditText) findViewById(R.id.et_ecpss_asset);

        /******************************欠别人钱***********************************/
        layoutOweOther = (LinearLayout) findViewById(R.id.layout_owe_other);
        tvOweOtherChangeTime = (TextView) findViewById(R.id.tv_owe_other_change_time);
        etOweOtherWhat = (EditText) findViewById(R.id.et_owe_other_what);
        tvOweOtherAsset = (TextView) findViewById(R.id.tv_owe_other_asset);
        etOweOtherAsset = (EditText) findViewById(R.id.et_owe_other_asset);

        /******************************我是债主***********************************/
        layoutOweMe = (LinearLayout) findViewById(R.id.layout_owe_me);
        tvOweMeChangeTime = (TextView) findViewById(R.id.tv_owe_me_change_time);
        etOweMeWhat = (EditText) findViewById(R.id.et_owe_me_what);
        tvOweMeAsset = (TextView) findViewById(R.id.tv_owe_me_asset);
        etOweMeAsset = (EditText) findViewById(R.id.et_owe_me_asset);

        /******************************个人现金布局***********************************/
        layoutMe = (LinearLayout) findViewById(R.id.layout_me);
        tvMeChangeTime = (TextView) findViewById(R.id.tv_me_change_time);
        tvMeAsset = (TextView) findViewById(R.id.tv_me_asset);
        etMeAsset = (EditText) findViewById(R.id.et_me_asset);

    }

    private void initListener(){
        // 标题
        btnBack.setOnClickListener(this);
        btnFinish.setOnClickListener(this);


        // 信用卡布局
        tvCreditChangeTime.setOnClickListener(this);

        // 储蓄卡布局
        tvDepositChangeTime.setOnClickListener(this);

        // 借贷公司布局
        tvCompanyChangeTime.setOnClickListener(this);

        // 第三方支付布局
        tvEcpssChangeTime.setOnClickListener(this);

        // 欠别人钱
        tvOweOtherChangeTime.setOnClickListener(this);

        // 我是债主
        tvOweMeChangeTime.setOnClickListener(this);

        // 个人现金布局
        tvMeChangeTime.setOnClickListener(this);
    }

    private void initData(){
        /******************************标题***********************************/
        btnMenu.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);
        btnFinish.setVisibility(View.VISIBLE);

        btnFinish.setText("完成");
        tvTitle.setText("修改资产");

//        tvAssetType.setVisibility(View.GONE);                                           // 选择"资产类型"
        tvAssetType.setText(assetType);

        tableEx = new TableEx(AccountBookApplication.getContext());

        cursor = tableEx.Query(Constant.TABLE_ASSETS, null,
                "user=? and _id=?", new String[]{AccountBookApplication.getUserInfo().getUserName(), id+""},
                null, null, null);
        try {
            if (cursor.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                cursor.moveToFirst();

                changeTime = cursor.getString(3);
                what = cursor.getString(4);
                bankNum = cursor.getString(5);
                asset = cursor.getString(6);
            }else{
                ToastUtil.show("获取参数异常");
                return;
            }
        }catch (Exception e){
            System.out.println("获取此id出现状况:"+e.getMessage());
        }


        if(assetType.equals(Constant.CREDIT)){                                          // 信用卡
            layoutCreditCard.setVisibility(View.VISIBLE);
            layoutDepositCard.setVisibility(View.GONE);
            layoutCompany.setVisibility(View.GONE);
            layoutEcpss.setVisibility(View.GONE);
            layoutOweOther.setVisibility(View.GONE);
            layoutOweMe.setVisibility(View.GONE);
            layoutMe.setVisibility(View.GONE);

            tvCreditChangeTime.setText(changeTime);
            etCreditWhat.setText(what);
            etCreditCardNum.setText(bankNum);
            etCreditAsset.setText(asset);
        }else if(assetType.equals(Constant.DEPOSIT)){                                   // 储蓄卡
            layoutCreditCard.setVisibility(View.GONE);
            layoutDepositCard.setVisibility(View.VISIBLE);
            layoutCompany.setVisibility(View.GONE);
            layoutEcpss.setVisibility(View.GONE);
            layoutOweOther.setVisibility(View.GONE);
            layoutOweMe.setVisibility(View.GONE);
            layoutMe.setVisibility(View.GONE);

            tvDepositChangeTime.setText(changeTime);
            etDepositWhat.setText(what);
            etDepositCardNum.setText(bankNum);
            etDepositAsset.setText(asset);
        }else if(assetType.equals(Constant.COMPANY)){                                   // 借贷公司
            layoutCreditCard.setVisibility(View.GONE);
            layoutDepositCard.setVisibility(View.GONE);
            layoutCompany.setVisibility(View.VISIBLE);
            layoutEcpss.setVisibility(View.GONE);
            layoutOweOther.setVisibility(View.GONE);
            layoutOweMe.setVisibility(View.GONE);
            layoutMe.setVisibility(View.GONE);

            tvCompanyChangeTime.setText(changeTime);
            etCompanyWhat.setText(what);
            etCompanyAsset.setText(asset);
        }else if(assetType.equals(Constant.ECPSS)){                                     // 第三方支付
            layoutCreditCard.setVisibility(View.GONE);
            layoutDepositCard.setVisibility(View.GONE);
            layoutCompany.setVisibility(View.GONE);
            layoutEcpss.setVisibility(View.VISIBLE);
            layoutOweOther.setVisibility(View.GONE);
            layoutOweMe.setVisibility(View.GONE);
            layoutMe.setVisibility(View.GONE);

            tvEcpssChangeTime.setText(changeTime);
            etEcpssWhat.setText(what);
            etEcpssAsset.setText(asset);
        }else if(assetType.equals(Constant.OWEOTHER)){                                     // 欠别人钱
            layoutCreditCard.setVisibility(View.GONE);
            layoutDepositCard.setVisibility(View.GONE);
            layoutCompany.setVisibility(View.GONE);
            layoutEcpss.setVisibility(View.GONE);
            layoutOweOther.setVisibility(View.VISIBLE);
            layoutOweMe.setVisibility(View.GONE);
            layoutMe.setVisibility(View.GONE);

            tvOweOtherChangeTime.setText(changeTime);
            etOweOtherWhat.setText(what);
            etOweOtherAsset.setText(asset);
        }else if(assetType.equals(Constant.OWEME)){                                     // 我是债主
            layoutCreditCard.setVisibility(View.GONE);
            layoutDepositCard.setVisibility(View.GONE);
            layoutCompany.setVisibility(View.GONE);
            layoutEcpss.setVisibility(View.GONE);
            layoutOweOther.setVisibility(View.GONE);
            layoutOweMe.setVisibility(View.VISIBLE);
            layoutMe.setVisibility(View.GONE);

            tvOweMeChangeTime.setText(changeTime);
            etOweMeWhat.setText(what);
            etOweMeAsset.setText(asset);
        }else if(assetType.equals(Constant.ME)){                                     // 个人现金
            layoutCreditCard.setVisibility(View.GONE);
            layoutDepositCard.setVisibility(View.GONE);
            layoutCompany.setVisibility(View.GONE);
            layoutEcpss.setVisibility(View.GONE);
            layoutOweOther.setVisibility(View.GONE);
            layoutOweMe.setVisibility(View.GONE);
            layoutMe.setVisibility(View.VISIBLE);

            tvMeChangeTime.setText(changeTime);
            etMeAsset.setText(asset);
        }else
            ToastUtil.show("传过来的是未知＇资产类型＇!!!");


        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
        // Calendar calendar = Calendar.getInstance();
        // pvBankChangeTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦
        pvTime.setTitle("修改时间");         // 设置"标题"
        pvTime.setTime(new Date());         // 设置当前的时间，到时间选择器
        pvTime.setCyclic(false);
        pvTime.setCancelable(false);         // true:点击弹出"布局"外部，收回"布局";false:没反应
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {

            if(TimeCompare.timeCompare(getTime(date),getTime(new Date())) <= 0) {
                tvCreditChangeTime.setText(getTime(date));             // 信用卡布局
                tvDepositChangeTime.setText(getTime(date));            // 储蓄卡布局
                tvEcpssChangeTime.setText(getTime(date));               // 第三方支付布局
                tvOweOtherChangeTime.setText(getTime(date));            // 欠别人钱
                tvOweMeChangeTime.setText(getTime(date));               // 我是债主
                tvCompanyChangeTime.setText(getTime(date));             // 借贷公司布局
                tvMeChangeTime.setText(getTime(date));                  // 个人现金布局
            }else
                    ToastUtil.show("修改时间不能是未来时间");
            }
        });

    }

    /**
     * 将传进来的"时间"，按照一定的格式生成"时间字符串"
     * @param date 传进来的时间
     * @return
     */
    public static String getTime(Date date) {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }


    /**
     * 修改"资产"
     */
    public void change(){

        ContentValues values = new ContentValues();
        //   values.put("_id", id);                   // 主键可以不写
        values.put("user", AccountBookApplication.getUserInfo().getUserName());

        if (assetType.equals(Constant.CREDIT)) {                                          // 信用卡
            values.put("type", assetType);                                             // 什么布局
            values.put("changetime", tvCreditChangeTime.getText().toString()); // 修改金额时间
            values.put("what", etCreditWhat.getText().toString());              // 什么银行的信用卡
            values.put("cardnum", etCreditCardNum.getText().toString());        // 银行卡后面4位
            values.put("asset", etCreditAsset.getText().toString());            // 资源

            if(etCreditCardNum.getText().toString().length() != 4){
                ToastUtil.show("请输入银行卡后4位");
                return;
            }

            if(!TextUtils.isEmpty(etCreditAsset.getText().toString())){
                if(Float.valueOf(etCreditAsset.getText().toString()) > 0){
                    ToastUtil.show("资产请输入小于等于0");
                    return;
                }
            }else{
                ToastUtil.show("资产不能为空");
                return;
            }
        } else if (assetType.equals(Constant.DEPOSIT)) {                                   // 储蓄卡
            values.put("type", assetType);                                             // 什么布局
            values.put("changetime", tvDepositChangeTime.getText().toString()); // 修改金额时间
            values.put("what", etDepositWhat.getText().toString());              // 什么银行的储蓄卡
            values.put("cardnum", etDepositCardNum.getText().toString());        // 银行卡后面4位
            values.put("asset", etDepositAsset.getText().toString());            // 资源


            if(etDepositCardNum.getText().toString().length() != 4){
                ToastUtil.show("请输入银行卡后4位");
                return;
            }

            if(!TextUtils.isEmpty(etDepositAsset.getText().toString())){
                if(Float.valueOf(etDepositAsset.getText().toString()) < 0){
                    ToastUtil.show("资产请输入大于等于0");
                    return;
                }
            }else{
                ToastUtil.show("资产不能为空");
                return;
            }
        } else if (assetType.equals(Constant.COMPANY)) {                                   // 借贷公司
            values.put("type", assetType);                                             // 什么布局
            values.put("changetime", tvCompanyChangeTime.getText().toString()); // 修改金额时间
            values.put("what", etCompanyWhat.getText().toString());              // 什么借贷公司
            values.put("asset", etCompanyAsset.getText().toString());            // 资源

            if(!TextUtils.isEmpty(etCompanyAsset.getText().toString())){
                if(Float.valueOf(etCompanyAsset.getText().toString()) > 0){
                    ToastUtil.show("资产请输入小于等于0");
                    return;
                }
            }else{
                ToastUtil.show("资产不能为空");
                return;
            }
        } else if (assetType.equals(Constant.ECPSS)) {                                     // 第三方支付
            values.put("type", assetType);                                             // 什么布局
            values.put("changetime", tvEcpssChangeTime.getText().toString()); // 修改金额时间
            values.put("what", etEcpssWhat.getText().toString());              // 什么第三方支付
            values.put("asset", etEcpssAsset.getText().toString());            // 资源

            if(!TextUtils.isEmpty(etEcpssAsset.getText().toString())){
                if(Float.valueOf(etEcpssAsset.getText().toString()) < 0){
                    ToastUtil.show("资产请输入大于等于0");
                    return;
                }
            }else{
                ToastUtil.show("资产不能为空");
                return;
            }
        } else if (assetType.equals(Constant.OWEOTHER)) {                                     // 欠别人钱
            values.put("type", assetType);                                             // 什么布局
            values.put("changetime", tvOweOtherChangeTime.getText().toString()); // 修改金额时间
            values.put("what", etOweOtherWhat.getText().toString());              // 欠什么人钱
            values.put("asset", etOweOtherAsset.getText().toString());            // 资源

            if(!TextUtils.isEmpty(etOweOtherAsset.getText().toString())){
                if(Float.valueOf(etOweOtherAsset.getText().toString()) > 0){
                    ToastUtil.show("资产请输入小于等于0");
                    return;
                }
            }else{
                ToastUtil.show("资产不能为空");
                return;
            }
        } else if (assetType.equals(Constant.OWEME)) {                                     // 我是债主
            values.put("type", assetType);                                             // 什么布局
            values.put("changetime", tvOweMeChangeTime.getText().toString()); // 修改金额时间
            values.put("what", etOweMeWhat.getText().toString());              // 什么人欠我钱
            values.put("asset", etOweMeAsset.getText().toString());            // 资源

            if(!TextUtils.isEmpty(etOweMeAsset.getText().toString())){
                if(Float.valueOf(etOweMeAsset.getText().toString()) < 0){
                    ToastUtil.show("资产请输入大于等于0");
                    return;
                }
            }else{
                ToastUtil.show("资产不能为空");
                return;
            }
        } else if (assetType.equals(Constant.ME)) {                                     // 个人现金
            values.put("type", assetType);                                             // 什么布局
            values.put("changetime", tvMeChangeTime.getText().toString());              // 修改金额时间
            values.put("asset", etMeAsset.getText().toString());            // 资源

            if(!TextUtils.isEmpty(etMeAsset.getText().toString())){
                if(Float.valueOf(etMeAsset.getText().toString()) < 0){
                    ToastUtil.show("资产请输入大于等于0");
                    return;
                }
            }else{
                ToastUtil.show("资产不能为空");
                return;
            }
        } else {
            ToastUtil.show("没有选择资产类型");
            return;
        }

        int num = tableEx.Update(Constant.TABLE_ASSETS, values, "_id=?", new String[]{id+""});

        if(num != 0){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            bundle.putBoolean(CHANGE_ASSET,true);
            intent.putExtras(bundle);
            setResult(OK,intent);

            ToastUtil.show("修改成功");
            finish();
        }else
            ToastUtil.show("修改失败");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:                                         // 退出本Activity
                finish();
                break;

            case R.id.btn_title_confirm:                               // 确认"完成"
                change();
                break;

            /******************************修改时间***********************************/
            case R.id.tv_credit_change_time:                        // 信用卡布局
            case R.id.tv_deposit_change_time:                       // 储蓄卡布局
            case R.id.tv_company_change_time:                       // 借贷公司布局
            case R.id.tv_ecpss_change_time:                         // 第三方支付布局
            case R.id.tv_owe_other_change_time:                     // 欠别人钱
            case R.id.tv_owe_me_change_time:                        // 我是债主
            case R.id.tv_me_change_time:                            // 个人现金布局

                pvTime.show();
                break;
        }
    }

    // --------------------------点击"空白地方"隐藏输入法-----------------------------
    // 获取点击事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();                      // 获得焦点的控件
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    // 判定是否需要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    // 隐藏软键盘
    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);     // 隐藏输入法
        }
    }
}
