package com.shen.accountbook2.ui.fragment.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shen.accountbook2.R;
import com.shen.accountbook2.Utils.SharePrefUtil;
import com.shen.accountbook2.Utils.ToFormatUtil;
import com.shen.accountbook2.config.Constant;
import com.shen.accountbook2.db.biz.TableEx;
import com.shen.accountbook2.global.AccountBookApplication;
import com.shen.accountbook2.widget.OnWheelChangedListener;
import com.shen.accountbook2.widget.WheelView;
import com.shen.accountbook2.widget.adapters.ArrayWheelAdapter;


/**
 * Created by shen on 10/14 0014.
 */
public class ReportForD2_Activity extends BaseReportActivity implements OnWheelChangedListener {

    private WheelView wvYear;           // "年份"滚动选择器
    private WheelView wvMonth;          // "月份"滚动选择器

    private ListView lvDay;

    private CheckBox cbMonth;           // 勾选：查月份; 不勾选：查年份
    private RadioGroup rgTypeAndM_Y;    // 选择"类型"，或"月份/日子"
    private RadioButton rbType;
    private RadioButton rbM_Y;

    private Button btnQuery;            // 查询

    private int type;                   // 选择了 "类型"，或"月份/日子"
    private TextView tvAllPriceName;    // 本月/今年
    private TextView tvAllPrice;        // 本月/今年总消费

    Cursor cursor;

    private static float progressMax = 1;            // 最大值

    public ReportForD2_Activity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.report_day2);
        super.initView();

        mMeun.setVisibility(View.GONE);
        mBack.setVisibility(View.VISIBLE);

        wvYear = (WheelView) findViewById(R.id.wv_y);
        wvMonth = (WheelView) findViewById(R.id.wv_m);

        lvDay = (ListView) findViewById(R.id.lv);

        cbMonth = (CheckBox) findViewById(R.id.cb_m);
        rgTypeAndM_Y = (RadioGroup) findViewById(R.id.rg);
        rbType = (RadioButton) findViewById(R.id.rb_type);
        rbM_Y = (RadioButton) findViewById(R.id.rb_M_Y);

        btnQuery = (Button) findViewById(R.id.btn_query);

        tvAllPrice = (TextView) findViewById(R.id.tv_AllPrice);
        tvAllPriceName = (TextView) findViewById(R.id.tv_AllPriceName);

    }

    @Override
    public void initListener() {
        super.initListener();

        // 添加change事件
        wvYear.addChangingListener(this);
        wvMonth.addChangingListener(this);

        btnQuery.setOnClickListener(this);

        rgTypeAndM_Y.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_type:
                        type = 0;
                        break;
                    case R.id.rb_M_Y:
                        type = 1;
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        mTitle.setText("日报表2");

        ArrayWheelAdapter<String> yearAdapter = new ArrayWheelAdapter<String>(this, Constant.dialogYear);
        yearAdapter.setTextSize(20);                // 文本(字体)大小
        wvYear.setViewAdapter(yearAdapter);      // 添加适配器
        wvYear.setVisibleItems(1);               // 可见数目，默认是5
        wvYear.setCurrentItem(index_year);

        ArrayWheelAdapter<String> monthAdapter = new ArrayWheelAdapter<String>(this, Constant.dialogMonth);
        monthAdapter.setTextSize(20);                // 文本(字体)大小
        wvMonth.setViewAdapter(monthAdapter);      // 添加适配器
        wvMonth.setVisibleItems(1);               // 可见数目，默认是5
        wvMonth.setCurrentItem(index_month);

        cbMonth.setChecked(SharePrefUtil.getBoolean(AccountBookApplication.getContext(), SharePrefUtil.REPORT_KEY_2.YEAR_MONTH, false));

        // 初始化，选中的
        int type = SharePrefUtil.getInt(AccountBookApplication.getContext(),SharePrefUtil.REPORT_KEY_2.TYPE_MONTHANDDAY,1);
        switch (type){
            case 0: rbType.setChecked(true);
                break;
            case 1: rbM_Y.setChecked(true);
                break;
            default: rbM_Y.setChecked(true);
                break;
        }
    }

    /**
     * 查询
     */
    private void query() {
        TableEx tableEx = new TableEx(AccountBookApplication.getContext());
        String yOrym;

        if(cbMonth.isChecked()){
            yOrym = Constant.dialogYear[wvYear.getCurrentItem()] + "-" +
                    Constant.dialogMonth[wvMonth.getCurrentItem()] + "-%";
            if(rbM_Y.isChecked()) {
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price) as _id", "strftime('%d日',date)"},
                        "date like ? and user=?", new String[]{yOrym, AccountBookApplication.getUserInfo().getUserName()},
                        "strftime('%Y年%m月%d日',date)", null, null);

            }else if(rbType.isChecked()){
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price) as _id", "maintype"},
                        "date like ? and user=?", new String[]{yOrym, AccountBookApplication.getUserInfo().getUserName()},
                        "maintype", null, null);
            }

            // 获取本月总支出
            Cursor c = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price)"},
                    "date like ? and user=?", new String[]{yOrym, AccountBookApplication.getUserInfo().getUserName()},
                    null, null, null);
            try {
                if (!c.moveToFirst() == false) {
                    progressMax = Float.valueOf(c.getString(0));
                    tvAllPriceName.setText(Constant.dialogMonth[wvMonth.getCurrentItem()] + "月份消费了: ");
                    tvAllPrice.setText(ToFormatUtil.toDecimalFormat(progressMax, 2) + " 人民币");
                }else{
                    tvAllPriceName.setText(Constant.dialogMonth[wvMonth.getCurrentItem()] + "月份消费了: ");
                    tvAllPrice.setText("0 人民币");
                }
            }catch (Exception e){
                System.out.println("error1:"+e.getMessage());
            }
        }else{
            yOrym = Constant.dialogYear[wvYear.getCurrentItem()] + "-%";
            if(rbM_Y.isChecked()) {
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price) as _id","strftime('%m月',date)"},
                        "date like ? and user=?", new String[]{yOrym, AccountBookApplication.getUserInfo().getUserName()},
                        "strftime('%Y年%m月',date)", null, null);

            }else if(rbType.isChecked()){
                cursor = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price) as _id","maintype"},
                        "date like ? and user=?", new String[]{yOrym, AccountBookApplication.getUserInfo().getUserName()},
                        "maintype", null, null);
            }

            // 获取本年总支出
            Cursor c = tableEx.Query(Constant.TABLE_CONSUMPTION, new String[]{"sum(price)"},
                    "date like ? and user=?", new String[]{yOrym, AccountBookApplication.getUserInfo().getUserName()},
                    null, null, null);
            try {
                if (!c.moveToFirst() == false) {
                    progressMax = Float.valueOf(c.getString(0));
                    tvAllPriceName.setText( Constant.dialogYear[wvYear.getCurrentItem()]+"年消费了: ");
                    tvAllPrice.setText(ToFormatUtil.toDecimalFormat(progressMax,2)+" 人民币");
                }else{
                    tvAllPriceName.setText(Constant.dialogYear[wvYear.getCurrentItem()] + "年消费了: ");
                    tvAllPrice.setText("0 人民币");
                }
            }catch (Exception e){
                System.out.println("error2:"+e.getMessage());
            }

        }
        SharePrefUtil.saveBoolean(AccountBookApplication.getContext(), SharePrefUtil.REPORT_KEY_2.YEAR_MONTH, cbMonth.isChecked());

//        while (cursor.moveToNext()){
//            String sumPric = cursor.getString(0);
//            String strftime = cursor.getString(1);
//            System.out.println("sum(price):"+sumPric+"    strftime('%Y年%m月',date):"+strftime );
//        }

        lvDay.setAdapter(new MyCursorAdapter(getApplicationContext(), cursor));

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
            View view = inflater.inflate(R.layout.report_day2_item,parent,false); // listview中每一项的布局

            viewHolder.tvDay = (TextView) view.findViewById(R.id.tv_day);
            viewHolder.pbPrice = (ProgressBar) view.findViewById(R.id.pb_price);
            viewHolder.tvProgressbar = (TextView) view.findViewById(R.id.tv_progressbar);
            viewHolder.tvPrice = (TextView) view.findViewById(R.id.tv_Price);

            view.setTag(viewHolder); // 设置进去

            return view;
        }

        /**在绘制Item之前一定会调用bindView方法它在重绘的时候也同样被调用*/
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final ViewHolder viewHolder=(ViewHolder) view.getTag();   // 拿出来

            float sumPrice = Float.valueOf(cursor.getString(0));
            String dateOrType = cursor.getString(1);

            viewHolder.pbPrice.setMax(100);
            viewHolder.pbPrice.setProgress((int)((sumPrice/progressMax)*100));
            viewHolder.tvDay.setText(dateOrType);
            // sumPrice+"/"+progressMax+"="+
            viewHolder.tvProgressbar.setText(ToFormatUtil.toDecimalFormat((sumPrice/progressMax)*100,2)+"%");
            viewHolder.tvPrice.setText("-"+sumPrice);

        }

        static class ViewHolder{
            TextView tvDay;
            ProgressBar pbPrice;
            TextView tvProgressbar;
            TextView tvPrice;

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_query:
                query();

        }
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

    }
}
