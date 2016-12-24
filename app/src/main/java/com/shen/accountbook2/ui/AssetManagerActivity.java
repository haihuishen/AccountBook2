package com.shen.accountbook2.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.ToastUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;

/**
 * Created by shen on 11/2 0002.
 */
public class AssetManagerActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_CHANGE = 2;

    public static final int OK = 1;

    public static Context mContext;
    /******************************标题***********************************/
    private TextView tvTitle;
    private ImageButton btnMenu;
    private ImageButton btnBack;
    private Button btnAdd;

    private static Cursor cursor;
    private static Cursor cursorTotalAssets;        // 总资产
    private static TextView tvTotalAssets;
    private static ListView lvAsset;

    private static TableEx tableEx;
    private static MyCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_manager);

        mContext = this;
        initView();
        initListener();
        initDate();
    }

    private void initView(){
        /******************************标题***********************************/
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnAdd = (Button) findViewById(R.id.btn_title_confirm);


        tvTotalAssets = (TextView) findViewById(R.id.tv_total_assets);
        lvAsset = (ListView) findViewById(R.id.lv_asset);
    }

    private void initListener() {
        // 标题
        btnBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        lvAsset.setOnItemClickListener(this);
    }

    private void initDate(){
        /******************************标题***********************************/
        btnMenu.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setText("添加");
        tvTitle.setText("资产管理");

        tableEx = new TableEx(AccountBookApplication.getContext());

        // cast(sum(asset) as TEXT)--> 这样就不会变成"科学计数法"
        // sum(asset) -->asset 就算是 varchar(20),不是decimal(18,2)，使用sum(asset)后还是"会使用科学计数法"
        cursorTotalAssets = tableEx.Query(Constant.TABLE_ASSETS, new String[]{"cast(sum(asset) as TEXT)"},
                "user=?", new String[]{AccountBookApplication.getUserInfo().getUserName()},
                null, null, null);
        try {
            if (cursorTotalAssets.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                cursorTotalAssets.moveToFirst();
                if(cursorTotalAssets.getString(0) != null){        // 没记录，返回1，这里返回的是 "null"
                    if(Float.valueOf(cursorTotalAssets.getString(0)) >= 0) {
                        tvTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                        tvTotalAssets.setText("总资产: " + cursorTotalAssets.getString(0));
                    }else{
                        tvTotalAssets.setTextColor(this.getResources().getColor(R.color.red));
                        tvTotalAssets.setText("总资产: " + cursorTotalAssets.getString(0));
                    }
                }else{
                    tvTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                    tvTotalAssets.setText("总资产: 0");
                }
            }else{
                tvTotalAssets.setTextColor(this.getResources().getColor(R.color.forestgreen));
                tvTotalAssets.setText("总资产: 0");
            }
        }catch (Exception e){
            System.out.println("总资产error:"+e.getMessage());
        }


        cursor = tableEx.Query(Constant.TABLE_ASSETS, null,
                "user=?", new String[]{AccountBookApplication.getUserInfo().getUserName()},
                null, null, "type,changetime,asset DESC");

        adapter = new MyCursorAdapter(this, cursor);
        lvAsset.setAdapter(adapter);

////        listview分割线会在头部、数据item、及根部的底部打印，如果要取消头部分割线必须
////                先设置期方法
//        View headView = new View(this);
//        View footView = new View(this);
//        lvAsset.addHeaderView(headView, null, false);
//        lvAsset.addFooterView(footView, null, false);
////        注意：第三个参数必须为true，否则无效
//
//        //显示头部出现分割线
//        lvAsset.setHeaderDividersEnabled(true);
//        //禁止底部出现分割线
//        lvAsset.setFooterDividersEnabled(false);
    }

    //    (1)newView：并不是每次都被调用的，它只在实例化的时候调用,数据增加的时候也会调用,
//        但是在重绘(比如修改条目里的TextView的内容)的时候不会被调用
//    (2)bindView：从代码中可以看出在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用
//  CursorAdapter还有一个重要的方法 public void changeCursor (Cursor cursor)：
    public static class MyCursorAdapter extends CursorAdapter {

        //超重点，cursor 中查询出来的字段 必须有一个是"_id"， 没有的话，可以将"列"重命名
        // 如：——"sum(price) as _id"
        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        /**并不是每次都被调用的，它只在实例化的时候调用,数据增加的时候也会调用,
         但是在重绘(比如修改条目里的TextView的内容)的时候不会被调用*/
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // 获得 LayoutInflater 实例的三种方式:
            // LayoutInflater inflater = getLayoutInflater();  //调用Activity的getLayoutInflater()
            // LayoutInflater localinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // LayoutInflater inflater = LayoutInflater.from(context);

            ViewHolder viewHolder= new ViewHolder();
            // 将 layout 填充成"View"
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate(R.layout.item_asset,parent,false); // listview中每一项的布局

            viewHolder.layoutBg = (RelativeLayout) view.findViewById(R.id.layout_bg);
            viewHolder.tvAssetType = (TextView) view.findViewById(R.id.tv_asset_type);
            viewHolder.tvChangeTime = (TextView) view.findViewById(R.id.tv_change_time);
            viewHolder.tvWhat = (TextView) view.findViewById(R.id.tv_what);
            viewHolder.tvBankNum = (TextView) view.findViewById(R.id.tv_bank_num);
            viewHolder.tvAsset = (TextView) view.findViewById(R.id.tv_asset);
            viewHolder.ibDelete = (ImageButton) view.findViewById(R.id.ib_delete);

            view.setTag(viewHolder); // 设置进去

            return view;
        }

        /**在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用*/
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final ViewHolder viewHolder=(ViewHolder) view.getTag();   // 拿出来

            final String assetType = cursor.getString(2);
            final String changeTime = cursor.getString(3);
            final String what = cursor.getString(4);
            final String asset = cursor.getString(6);

            viewHolder.tvAssetType.setText(assetType);
            viewHolder.tvChangeTime.setText("修改时间: " + changeTime);
            // "信用卡"  "储蓄卡"
            if(cursor.getString(2).equals(Constant.CREDIT) || cursor.getString(2).equals(Constant.DEPOSIT) ){
                viewHolder.tvBankNum.setVisibility(View.VISIBLE);
                viewHolder.tvBankNum.setText("尾号:" + cursor.getString(5));
            }else
                viewHolder.tvBankNum.setVisibility(View.GONE);

            if (assetType.equals(Constant.CREDIT)) {                         // 信用卡 : 蓝色
                viewHolder.layoutBg.setBackgroundResource(R.drawable.bg_bule_asset);
                viewHolder.tvWhat.setText(what);
                viewHolder.tvAsset.setTextColor(mContext.getResources().getColor(R.color.red));
                viewHolder.tvAsset.setText("资金: " + asset);
            } else if (assetType.equals(Constant.DEPOSIT)) {                 // 储蓄卡 : 绿色
                viewHolder.layoutBg.setBackgroundResource(R.drawable.bg_green);
                viewHolder.tvWhat.setText(what);
                viewHolder.tvAsset.setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                viewHolder.tvAsset.setText("资金: " + asset);
            } else if (assetType.equals(Constant.COMPANY)) {                 // 借贷公司 : 粉色
                viewHolder.layoutBg.setBackgroundResource(R.drawable.bg_pink_asset);
                viewHolder.tvWhat.setText(what);
                viewHolder.tvAsset.setTextColor(mContext.getResources().getColor(R.color.red));
                viewHolder.tvAsset.setText("资金: " + asset);
            } else if (assetType.equals(Constant.ECPSS)) {                   // 第三方支付 : 紫色purple
                viewHolder.layoutBg.setBackgroundResource(R.drawable.bg_purple);
                viewHolder.tvWhat.setText(what);
                viewHolder.tvAsset.setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                viewHolder.tvAsset.setText("资金: " + asset);
            } else if (assetType.equals(Constant.OWEOTHER)) {               // 欠别人钱 : 棕色
                viewHolder.layoutBg.setBackgroundResource(R.drawable.bg_brown);
                viewHolder.tvWhat.setText("债主: " + what);
                viewHolder.tvAsset.setTextColor(mContext.getResources().getColor(R.color.red));
                viewHolder.tvAsset.setText("资金: " + asset);
            } else if (assetType.equals(Constant.OWEME)) {                   // 我是债主 : 深粉色
                viewHolder.layoutBg.setBackgroundResource(R.drawable.bg_deeppink);
                viewHolder.tvWhat.setText("欠债人: " + what);
                viewHolder.tvAsset.setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                viewHolder.tvAsset.setText("资金: " + asset);
            } else if (assetType.equals(Constant.ME)) {                      // 个人现金 : 橙色
                viewHolder.layoutBg.setBackgroundResource(R.drawable.bg_orange_asset);
                viewHolder.tvWhat.setText(what);
                viewHolder.tvAsset.setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                viewHolder.tvAsset.setText("资金: " + asset);
            }

            // 删除"资产"
            viewHolder.ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = 0;
                    if(assetType.equals(Constant.ME)) {
                        num = tableEx.Delete(Constant.TABLE_ASSETS,
                                "user=? and type=? and changetime=? and asset=?",
                                new String[]{AccountBookApplication.getUserInfo().getUserName(),
                                        assetType, changeTime, asset});
                    }else{
                        num = tableEx.Delete(Constant.TABLE_ASSETS,
                                "user=? and type=? and changetime=? and what=? and asset=?",
                                new String[]{AccountBookApplication.getUserInfo().getUserName(),
                                        assetType, changeTime, what, asset});
                    }
                    if(num != 0){
                        ToastUtil.show("删除资产成功");
                        refresh();
                    }
                    else
                        ToastUtil.show("删除资产失败");
                }
            });
        }

        static class ViewHolder{
            RelativeLayout layoutBg;    // 背景

            TextView tvAssetType;       // 资金类型
            TextView tvChangeTime;       // 修改时间
            TextView tvWhat;            // 债主：公司/人
            TextView tvBankNum;         // 如果是银行卡：这个银行卡后4位
            TextView tvAsset;           // 资金

            ImageButton ibDelete;       // 删除
        }
    }

    /**
     * 刷新
     */
    private static void refresh(){

        ToastUtil.show("刷新");

        // cast(sum(asset) as TEXT)--> 这样就不会变成"科学计数法"
        // sum(asset) -->asset 就算是 varchar(20),不是decimal(18,2)，使用sum(asset)后还是"会使用科学计数法"
        cursorTotalAssets = tableEx.Query(Constant.TABLE_ASSETS, new String[]{"cast(sum(asset) as TEXT)"},
                "user=?", new String[]{AccountBookApplication.getUserInfo().getUserName()},
                null, null, null);
        try {
            if (cursorTotalAssets.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                cursorTotalAssets.moveToFirst();
                if(cursorTotalAssets.getString(0) != null){        // 没记录，返回1，这里的返回的是 "null"
                    if(Float.valueOf(cursorTotalAssets.getString(0)) >= 0) {
                        tvTotalAssets.setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                        tvTotalAssets.setText("总资产: " + cursorTotalAssets.getString(0));
                    }else{
                        tvTotalAssets.setTextColor(mContext.getResources().getColor(R.color.red));
                        tvTotalAssets.setText("总资产: " + cursorTotalAssets.getString(0));
                    }
                }else{
                    tvTotalAssets.setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                    tvTotalAssets.setText("总资产: 0");
                }
            }else{
                tvTotalAssets.setTextColor(mContext.getResources().getColor(R.color.forestgreen));
                tvTotalAssets.setText("总资产: 0");
            }
        }catch (Exception e){
            System.out.println("总资产error:"+e.getMessage());
        }

        cursor = tableEx.Query(Constant.TABLE_ASSETS, null,
                "user=?", new String[]{AccountBookApplication.getUserInfo().getUserName()},
                null, null, "type,changetime,asset DESC");

        adapter = new MyCursorAdapter(mContext, cursor);
        lvAsset.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ADD){
            if(resultCode == AddAssetActivity.OK){
                Bundle bundle = data.getExtras();
                boolean b = bundle.getBoolean(AddAssetActivity.ADD_ASSET);
                if(b) {
                    refresh();
                }
            }
        }
        if(requestCode == REQUEST_CHANGE){
            if(resultCode == ChangeAssetActivity.OK){
                Bundle bundle = data.getExtras();
                boolean b = bundle.getBoolean(ChangeAssetActivity.CHANGE_ASSET);
                if(b) {
                    refresh();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_back:                                         // 退出本Activity
                intent = new Intent();
                Bundle bundle = new Bundle();

                bundle.putBoolean("refresh",true);
                intent.putExtras(bundle);
                setResult(OK,intent);
                finish();
                break;

            case R.id.btn_title_confirm:                               // 确认"添加"
                intent = new Intent(mContext, AddAssetActivity.class);
                startActivityForResult(intent,REQUEST_ADD);
                ToastUtil.show("确认");
                break;
        }
    }


    //  listview 点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ToastUtil.show("position:" + position);
        MyCursorAdapter.ViewHolder viewHolder=(MyCursorAdapter.ViewHolder) view.getTag();   // 拿出来
//        ToastUtil.show("资产:" + viewHolder.tvAsset.getText().toString());

        Cursor c;
        int _id = 0;

        String what;

        String assetType = viewHolder.tvAssetType.getText().toString();
        String changeTime = viewHolder.tvChangeTime.getText().toString().split(":")[1].trim();     // 修改时间: 2016-11-07 trim()去掉首尾空格
        if(viewHolder.tvWhat.getText().toString().split(":").length == 1) {
            what = viewHolder.tvWhat.getText().toString();
        }else{
            what = viewHolder.tvWhat.getText().toString().split(":")[1].trim();
        }
        String asset = viewHolder.tvAsset.getText().toString().split(":")[1].trim();    // 资金: 1236 trim()去掉首尾空格

        if(assetType.equals(Constant.ME)) {
            c = tableEx.Query(Constant.TABLE_ASSETS,new String[]{"_id"},
                    "user=? and type=? and changetime=? and asset=?",
                    new String[]{AccountBookApplication.getUserInfo().getUserName(),
                            assetType, changeTime, asset},null,null,null);
        }else{
            c = tableEx.Query(Constant.TABLE_ASSETS,new String[]{"_id"},
                    "user=? and type=? and changetime=? and what=? and asset=?",
                    new String[]{AccountBookApplication.getUserInfo().getUserName(),
                            assetType, changeTime, what, asset},null,null,null);
        }
        try {
            if (c.getCount() != 0) {        // 查询：带"函数"字段，就算"没记录"，返回的也是"1"
                c.moveToFirst();
                if(c.getInt(0) != 0){        // 没记录，返回1，这里返回的是 "0"
                    _id = c.getInt(0);

                    Intent intent = new Intent();
                    intent.setClass(mContext,ChangeAssetActivity.class);
                    // 绑定成一捆数据
                    Bundle data = new Bundle();
                    data.putString("assetType",assetType);
                    data.putInt("_id",_id);
                    intent.putExtras(data);

                    startActivityForResult(intent, REQUEST_CHANGE);
                }
            }
        }catch (Exception e){
            System.out.println("总资产error:"+e.getMessage());
        }
    }

    // 按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            bundle.putBoolean("refresh",true);
            intent.putExtras(bundle);
            setResult(OK,intent);

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
