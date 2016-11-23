package com.shen.accountbook2.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.TimeCompare;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shen on 10/31 0031.
 */
public class AddAssetActivity extends Activity implements View.OnClickListener{

    public static final int OK = 1;
    public static final String ADD_ASSET= "addAsset";

    TableEx tableEx;
    /******************************标题***********************************/
    TextView tvTitle;
    ImageButton btnMenu;
    ImageButton btnBack;
    Button btnFinish;

    /******************************选择资产类型***********************************/
    TextView tvAssetType;                                   // 选择资产类型
    /** 条件选择器*/
    OptionsPickerView pvOptionsAssetType;                  // 条件选择器
    ArrayList<String> assetTypeList;                        // 资产类型列表

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

        // 选择资产类型
        tvAssetType.setOnClickListener(this);

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
        tvTitle.setText("添加资产");

        tableEx = new TableEx(AccountBookApplication.getContext());
        /******************************选择资产类型***********************************/
        assetTypeList = new ArrayList<String>();
        assetTypeList.add(Constant.CREDIT);
        assetTypeList.add(Constant.DEPOSIT);
        assetTypeList.add(Constant.COMPANY);
        assetTypeList.add(Constant.ECPSS);
        assetTypeList.add(Constant.OWEOTHER);
        assetTypeList.add(Constant.OWEME);
        assetTypeList.add(Constant.ME);

        //选项选择器
        pvOptionsAssetType = new OptionsPickerView(this);
        pvOptionsAssetType.setCancelable(false);     // true:点击弹出"布局"外部，收回"布局";false:没反应
        //三级联动效果
        pvOptionsAssetType.setPicker(assetTypeList,null, null, true);

        //设置选择的三级单位
        // pwOptions.setLabels("主类型", "次类型", "次次类型");
        pvOptionsAssetType.setTitle("资产类型");
        pvOptionsAssetType.setCyclic(false, false, true);         // 三级联动，哪个可以循环滚动

        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptionsAssetType.setSelectOptions(1, 0, 0);
        //选项选择器后回调
        pvOptionsAssetType.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = assetTypeList.get(options1);
                //       +"-"+ AccountBookApplication.getListType1List().get(options1).get(option2);
                //     + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
                tvAssetType.setText(tx);

                if(tx.equals(Constant.CREDIT)){                                          // 信用卡
                    layoutCreditCard.setVisibility(View.VISIBLE);
                    layoutDepositCard.setVisibility(View.GONE);
                    layoutCompany.setVisibility(View.GONE);
                    layoutEcpss.setVisibility(View.GONE);
                    layoutOweOther.setVisibility(View.GONE);
                    layoutOweMe.setVisibility(View.GONE);
                    layoutMe.setVisibility(View.GONE);
                }else if(tx.equals(Constant.DEPOSIT)){                                   // 储蓄卡
                    layoutCreditCard.setVisibility(View.GONE);
                    layoutDepositCard.setVisibility(View.VISIBLE);
                    layoutCompany.setVisibility(View.GONE);
                    layoutEcpss.setVisibility(View.GONE);
                    layoutOweOther.setVisibility(View.GONE);
                    layoutOweMe.setVisibility(View.GONE);
                    layoutMe.setVisibility(View.GONE);
                }else if(tx.equals(Constant.COMPANY)){                                   // 借贷公司
                    layoutCreditCard.setVisibility(View.GONE);
                    layoutDepositCard.setVisibility(View.GONE);
                    layoutCompany.setVisibility(View.VISIBLE);
                    layoutEcpss.setVisibility(View.GONE);
                    layoutOweOther.setVisibility(View.GONE);
                    layoutOweMe.setVisibility(View.GONE);
                    layoutMe.setVisibility(View.GONE);
                }else if(tx.equals(Constant.ECPSS)){                                     // 第三方支付
                    layoutCreditCard.setVisibility(View.GONE);
                    layoutDepositCard.setVisibility(View.GONE);
                    layoutCompany.setVisibility(View.GONE);
                    layoutEcpss.setVisibility(View.VISIBLE);
                    layoutOweOther.setVisibility(View.GONE);
                    layoutOweMe.setVisibility(View.GONE);
                    layoutMe.setVisibility(View.GONE);
                }else if(tx.equals(Constant.OWEOTHER)){                                     // 欠别人钱
                    layoutCreditCard.setVisibility(View.GONE);
                    layoutDepositCard.setVisibility(View.GONE);
                    layoutCompany.setVisibility(View.GONE);
                    layoutEcpss.setVisibility(View.GONE);
                    layoutOweOther.setVisibility(View.VISIBLE);
                    layoutOweMe.setVisibility(View.GONE);
                    layoutMe.setVisibility(View.GONE);
                }else if(tx.equals(Constant.OWEME)){                                     // 我是债主
                    layoutCreditCard.setVisibility(View.GONE);
                    layoutDepositCard.setVisibility(View.GONE);
                    layoutCompany.setVisibility(View.GONE);
                    layoutEcpss.setVisibility(View.GONE);
                    layoutOweOther.setVisibility(View.GONE);
                    layoutOweMe.setVisibility(View.VISIBLE);
                    layoutMe.setVisibility(View.GONE);
                }else if(tx.equals(Constant.ME)){                                     // 个人现金
                    layoutCreditCard.setVisibility(View.GONE);
                    layoutDepositCard.setVisibility(View.GONE);
                    layoutCompany.setVisibility(View.GONE);
                    layoutEcpss.setVisibility(View.GONE);
                    layoutOweOther.setVisibility(View.GONE);
                    layoutOweMe.setVisibility(View.GONE);
                    layoutMe.setVisibility(View.VISIBLE);
                }else
                ToastUtil.show("返回的是啥啊!!!");
            }
        });

        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
        // Calendar calendar = Calendar.getInstance();
        // pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦
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
     * 添加"资产"
     */
    public void add(){

        String tx = tvAssetType.getText().toString();
        if(!TextUtils.isEmpty(tx)) {

            ContentValues values = new ContentValues();
            //   values.put("_id", id);                   // 主键可以不写
            values.put("user", AccountBookApplication.getUserInfo().getUserName());

            if (tx.equals(Constant.CREDIT)) {                                          // 信用卡
                values.put("type", tx);                                             // 什么布局
                values.put("changetime", tvCreditChangeTime.getText().toString()); // 修改金额时间
                values.put("what", etCreditWhat.getText().toString());              // 什么银行的信用卡
                values.put("cardnum", etCreditCardNum.getText().toString());        // 银行卡后面4位
                values.put("asset", etCreditAsset.getText().toString());            // 资产

                if(TextUtils.isEmpty(tvCreditChangeTime.getText().toString()))
                    ToastUtil.show("修改时间不能为空");
                if(TextUtils.isEmpty(etCreditWhat.getText().toString()))
                    ToastUtil.show("银行不能为空");
                if(etCreditCardNum.getText().toString().length() != 4){
                    ToastUtil.show("请输入银行卡后4位");
                    return;
                }
                if(!TextUtils.isEmpty(etCreditAsset.getText().toString())){
                    if(Float.valueOf(etCreditAsset.getText().toString()) > 0) {
                        ToastUtil.show("资产请输入小于等于0");
                        return;
                    }
                }else{
                    ToastUtil.show("资产不能为空");
                    return;
                }
            } else if (tx.equals(Constant.DEPOSIT)) {                                   // 储蓄卡
                values.put("type", tx);                                             // 什么布局
                values.put("changetime", tvDepositChangeTime.getText().toString()); // 修改金额时间
                values.put("what", etDepositWhat.getText().toString());              // 什么银行的储蓄卡
                values.put("cardnum", etDepositCardNum.getText().toString());        // 银行卡后面4位
                values.put("asset", etDepositAsset.getText().toString());            // 资产

                if(TextUtils.isEmpty(tvDepositChangeTime.getText().toString()))
                    ToastUtil.show("修改时间不能为空");
                if(TextUtils.isEmpty(etDepositWhat.getText().toString()))
                    ToastUtil.show("银行不能为空");
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
            } else if (tx.equals(Constant.COMPANY)) {                                   // 借贷公司
                values.put("type", tx);                                             // 什么布局
                values.put("changetime", tvCompanyChangeTime.getText().toString()); // 修改金额时间
                values.put("what", etCompanyWhat.getText().toString());              // 什么借贷公司
                values.put("asset", etCompanyAsset.getText().toString());            // 资产

                if(TextUtils.isEmpty(tvCompanyChangeTime.getText().toString()))
                    ToastUtil.show("修改时间不能为空");
                if(TextUtils.isEmpty(etCompanyWhat.getText().toString()))
                    ToastUtil.show("借贷公司不能为空");
                if(!TextUtils.isEmpty(etCompanyAsset.getText().toString())){
                    if(Float.valueOf(etCompanyAsset.getText().toString()) > 0) {
                        ToastUtil.show("资产请输入小于等于0");
                        return;
                    }
                }else{
                    ToastUtil.show("资产不能为空");
                    return;
                }
            } else if (tx.equals(Constant.ECPSS)) {                                     // 第三方支付
                values.put("type", tx);                                             // 什么布局
                values.put("changetime", tvEcpssChangeTime.getText().toString()); // 修改金额时间
                values.put("what", etEcpssWhat.getText().toString());              // 什么第三方支付
                values.put("asset", etEcpssAsset.getText().toString());            // 资产

                if(TextUtils.isEmpty(tvEcpssChangeTime.getText().toString()))
                    ToastUtil.show("修改时间不能为空");
                if(TextUtils.isEmpty(etEcpssWhat.getText().toString()))
                    ToastUtil.show("第三方支付不能为空");
                if(!TextUtils.isEmpty(etEcpssAsset.getText().toString())){
                    if(Float.valueOf(etEcpssAsset.getText().toString()) < 0){
                        ToastUtil.show("资产请输入大于等于0");
                        return;
                    }
                }else{
                    ToastUtil.show("资产不能为空");
                    return;
                }
            } else if (tx.equals(Constant.OWEOTHER)) {                                     // 欠别人钱
                values.put("type", tx);                                             // 什么布局
                values.put("changetime", tvOweOtherChangeTime.getText().toString()); // 修改金额时间
                values.put("what", etOweOtherWhat.getText().toString());              // 欠什么人钱
                values.put("asset", etOweOtherAsset.getText().toString());            // 资产

                if(TextUtils.isEmpty(tvOweOtherChangeTime.getText().toString()))
                    ToastUtil.show("修改时间不能为空");
                if(TextUtils.isEmpty(etOweOtherWhat.getText().toString()))
                    ToastUtil.show("债主不能为空");
                if(!TextUtils.isEmpty(etOweOtherAsset.getText().toString())){
                    if(Float.valueOf(etOweOtherAsset.getText().toString()) > 0) {
                        ToastUtil.show("资产请输入小于等于0");
                        return;
                    }
                }else{
                    ToastUtil.show("资产不能为空");
                    return;
                }
            } else if (tx.equals(Constant.OWEME)) {                                     // 我是债主
                values.put("type", tx);                                                         // 什么布局
                values.put("changetime", tvOweMeChangeTime.getText().toString());           // 修改金额时间
                values.put("what", etOweMeWhat.getText().toString());                       // 什么人欠我钱
                values.put("asset", etOweMeAsset.getText().toString());                     // 资产

                if(TextUtils.isEmpty(tvOweMeChangeTime.getText().toString()))
                    ToastUtil.show("修改时间不能为空");
                if(TextUtils.isEmpty(etOweMeWhat.getText().toString()))
                    ToastUtil.show("欠债人不能为空");
                if(!TextUtils.isEmpty(etOweMeAsset.getText().toString())){
                    if(Float.valueOf(etOweMeAsset.getText().toString()) < 0){
                        ToastUtil.show("资产请输入大于等于0");
                        return;
                    }
                }else{
                    ToastUtil.show("资产不能为空");
                    return;
                }
            } else if (tx.equals(Constant.ME)) {                                     // 个人现金
                values.put("type", tx);                                                     // 什么布局
                values.put("changetime", tvMeChangeTime.getText().toString());              // 修改金额时间
                values.put("asset", etMeAsset.getText().toString());                        // 资产

                if(TextUtils.isEmpty(tvMeChangeTime.getText().toString()))
                    ToastUtil.show("修改时间不能为空");
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

            long num = tableEx.Add(Constant.TABLE_ASSETS, values);

            if(num != 0){
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putBoolean(ADD_ASSET,true);
                intent.putExtras(bundle);
                setResult(OK,intent);

                ToastUtil.show("添加成功");
                finish();
            }else
                ToastUtil.show("添加失败");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:                                         // 退出本Activity
                finish();
                break;

            case R.id.btn_title_confirm:                               // 确认"添加"
                add();
                ToastUtil.show("确认");
                break;

            case R.id.tv_asset_type:                                   // 选择资产类型
                pvOptionsAssetType.show();
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
